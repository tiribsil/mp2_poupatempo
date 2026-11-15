const { createApp } = Vue;

createApp({
    data() {
        return {
            listaDeTiposDeAtendimento: [],
            identificadorTipoSelecionado: "",
            atendimentoPrioritario: false,
            senhaEmitida: null,
            mensagemDeErro: "",
            mensagemDeSucesso: "",
            carregando: false
        };
    },
    mounted() { // Executa quando carrega a página ligada à tag
        this.carregarTiposDeAtendimento();
    },
    methods: {
        async carregarTiposDeAtendimento() {
            try {
                const resposta = await fetch("api/emissao");
                if (!resposta.ok) {
                    throw new Error("Erro ao carregar tipos de atendimento.");
                }
                this.listaDeTiposDeAtendimento = await resposta.json();
                if (this.listaDeTiposDeAtendimento.length > 0) {
                    this.identificadorTipoSelecionado = this.listaDeTiposDeAtendimento[0].identificador;
                }
            } catch (erro) {
                this.mensagemDeErro = erro.message;
            }
        },

        async emitirSenha() {
            this.mensagemDeErro = "";
            this.mensagemDeSucesso = "";

            if (!this.identificadorTipoSelecionado) {
                this.mensagemDeErro = "Por favor, selecione um tipo de atendimento.";
                return;
            }

            this.carregando = true;

            try {
                const parametros = new URLSearchParams();
                parametros.append("tipoAtendimentoId", this.identificadorTipoSelecionado);
                parametros.append("prioritario", this.atendimentoPrioritario);

                const resposta = await fetch("api/emissao", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    body: parametros.toString()
                });

                const dados = await resposta.json();

                if (!resposta.ok) {
                    throw new Error(dados.erro || "Falha ao emitir a senha.");
                }

                this.senhaEmitida = dados;
                this.mensagemDeSucesso = "Senha emitida com sucesso!";
            } catch (erro) {
                this.mensagemDeErro = erro.message;
            } finally {
                this.carregando = false;
            }
        },

        limparRecibo() {
            this.senhaEmitida = null;
            this.mensagemDeSucesso = "";
            this.mensagemDeErro = "";
            this.atendimentoPrioritario = false;
        }
    }
}).mount("#aplicativo-emissao");
