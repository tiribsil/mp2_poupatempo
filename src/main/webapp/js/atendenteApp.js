const { createApp } = Vue;

createApp({
    data() {
        return {
            listaDeGuiches: [],
            numeroDoGuicheSelecionado: null,
            dadosDaFila: {
                quantidadeDeSenhasAguardandoCompativeis: 0,
                quantidadeDeSenhasPrioritariasAguardando: 0,
                quantidadeDeSenhasNormaisAguardando: 0,
                senhaAtualDoGuiche: null
            },
            mensagemDeErro: "",
            mensagemDeSucesso: "",
            temporizadorDeAtualizacao: null,
            carregando: false
        };
    },
    computed: {
        guicheSelecionado() {
            if (!this.numeroDoGuicheSelecionado) return null;
            return this.listaDeGuiches.find(g => g.numero === Number(this.numeroDoGuicheSelecionado));
        },
        possuiAtendimentoEmAndamento() {
            return this.dadosDaFila.senhaAtualDoGuiche != null;
        },
        statusSenhaAtual() {
            if (!this.dadosDaFila.senhaAtualDoGuiche) return "";
            return this.dadosDaFila.senhaAtualDoGuiche.status;
        }
    },
    mounted() {
        this.carregarGuiches();
    },
    beforeUnmount() {
        if (this.temporizadorDeAtualizacao) {
            clearInterval(this.temporizadorDeAtualizacao);
        }
    },
    methods: {
        async carregarGuiches() {
            try {
                const resposta = await fetch("api/atendente?acao=guiches");
                if (!resposta.ok) {
                    throw new Error("Erro ao carregar guichês.");
                }
                this.listaDeGuiches = await resposta.json();
                if (this.listaDeGuiches.length > 0) {
                    this.numeroDoGuicheSelecionado = this.listaDeGuiches[0].numero;
                    this.trocarGuiche();
                }
            } catch (erro) {
                this.mensagemDeErro = erro.message;
            }
        },

        trocarGuiche() {
            this.mensagemDeErro = "";
            this.mensagemDeSucesso = "";
            this.atualizarFila();

            if (this.temporizadorDeAtualizacao) {
                clearInterval(this.temporizadorDeAtualizacao);
            }
            this.temporizadorDeAtualizacao = setInterval(this.atualizarFila, 3000);
        },

        async atualizarFila() {
            if (!this.numeroDoGuicheSelecionado) return;

            try {
                const resposta = await fetch(`api/atendente?acao=fila&numeroGuiche=${this.numeroDoGuicheSelecionado}`);
                if (!resposta.ok) {
                    throw new Error("Erro ao consultar fila do guichê.");
                }
                this.dadosDaFila = await resposta.json();
            } catch (erro) {
                console.error("Falha na atualização automática da fila:", erro);
            }
        },

        async chamarProximaSenha() {
            this.mensagemDeErro = "";
            this.mensagemDeSucesso = "";
            this.carregando = true;

            try {
                const parametros = new URLSearchParams();
                parametros.append("acao", "proxima");
                parametros.append("numeroGuiche", this.numeroDoGuicheSelecionado);

                const resposta = await fetch("api/atendente", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: parametros.toString()
                });

                const dados = await resposta.json();

                if (!resposta.ok) {
                    throw new Error(dados.mensagem || "Erro ao chamar próxima senha.");
                }

                if (dados.mensagem && !dados.numeroFormatado) {
                    this.mensagemDeSucesso = dados.mensagem;
                } else {
                    this.mensagemDeSucesso = `Senha ${dados.numeroFormatado} chamada para o Guichê ${this.numeroDoGuicheSelecionado}!`;
                }

                await this.atualizarFila();
            } catch (erro) {
                this.mensagemDeErro = erro.message;
            } finally {
                this.carregando = false;
            }
        },

        async iniciarAtendimento() {
            if (!this.dadosDaFila.senhaAtualDoGuiche) return;

            this.mensagemDeErro = "";
            this.carregando = true;

            try {
                const parametros = new URLSearchParams();
                parametros.append("acao", "iniciar");
                parametros.append("senhaId", this.dadosDaFila.senhaAtualDoGuiche.identificador);

                const resposta = await fetch("api/atendente", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: parametros.toString()
                });

                const dados = await resposta.json();
                if (!resposta.ok) {
                    throw new Error(dados.mensagem || "Erro ao iniciar atendimento.");
                }

                this.mensagemDeSucesso = "Atendimento iniciado.";
                await this.atualizarFila();
            } catch (erro) {
                this.mensagemDeErro = erro.message;
            } finally {
                this.carregando = false;
            }
        },

        async finalizarAtendimento() {
            if (!this.dadosDaFila.senhaAtualDoGuiche) return;

            this.mensagemDeErro = "";
            this.carregando = true;

            try {
                const parametros = new URLSearchParams();
                parametros.append("acao", "finalizar");
                parametros.append("senhaId", this.dadosDaFila.senhaAtualDoGuiche.identificador);

                const resposta = await fetch("api/atendente", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: parametros.toString()
                });

                const dados = await resposta.json();
                if (!resposta.ok) {
                    throw new Error(dados.mensagem || "Erro ao finalizar atendimento.");
                }

                this.mensagemDeSucesso = "Atendimento finalizado com sucesso!";
                await this.atualizarFila();
            } catch (erro) {
                this.mensagemDeErro = erro.message;
            } finally {
                this.carregando = false;
            }
        },

        async marcarComoAusente() {
            if (!this.dadosDaFila.senhaAtualDoGuiche) return;

            this.mensagemDeErro = "";
            this.carregando = true;

            try {
                const parametros = new URLSearchParams();
                parametros.append("acao", "ausente");
                parametros.append("senhaId", this.dadosDaFila.senhaAtualDoGuiche.identificador);

                const resposta = await fetch("api/atendente", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: parametros.toString()
                });

                const dados = await resposta.json();
                if (!resposta.ok) {
                    throw new Error(dados.mensagem || "Erro ao marcar ausência.");
                }

                this.mensagemDeSucesso = "Usuário registrado como ausente.";
                await this.atualizarFila();
            } catch (erro) {
                this.mensagemDeErro = erro.message;
            } finally {
                this.carregando = false;
            }
        }
    }
}).mount("#aplicativo-atendente");
