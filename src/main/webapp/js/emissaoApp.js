const { createApp } = Vue;

createApp({
    data() {
        return {
            listaDeTiposDeAtendimento: [],
            identificadorTipoSelecionado: "",
            tipoSelecionado: null,
            atendimentoPrioritario: false,
            senhaEmitida: null,
            mensagemDeErro: "",
            mensagemDeSucesso: "",
            carregando: false,

            // Controle das etapas e pop-ups do totem
            etapaAtual: "selecao-tipo", // 'selecao-tipo' | 'documentos' | 'prioridade' | 'recibo'
            popupAtivo: null // null | 'sem-documentos' | 'info-prioridade'
        };
    },

    computed: {
        // Garante compatibilidade caso "documentos" venha como array ["RG", "CPF"] ou texto "RG, CPF"
        documentosFormatados() {
            const docs = this.tipoSelecionado && this.tipoSelecionado.documentos;
            if (!docs) {
                return [];
            }
            if (Array.isArray(docs)) {
                return docs;
            }
            if (typeof docs === "string") {
                return docs
                    .split(/[\n,;]+/)
                    .map((item) => item.trim())
                    .filter((item) => item.length > 0);
            }
            return [];
        }
    },

    mounted() {
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
            } catch (erro) {
                this.mensagemDeErro = erro.message;
            }
        },

        // Etapa 1 -> Etapa 2: Usuário toca no botão do serviço
        selecionarTipo(tipo) {
            this.mensagemDeErro = "";
            this.mensagemDeSucesso = "";
            this.tipoSelecionado = tipo;
            this.identificadorTipoSelecionado = tipo.identificador;
            this.etapaAtual = "documentos";
        },

        // Etapa 2 -> Etapa 3: Usuário confirma que está com os documentos
        confirmarDocumentos() {
            this.mensagemDeErro = "";
            this.etapaAtual = "prioridade";
        },

        // Etapa 3 -> Etapa 4: Usuário escolhe Convencional (false) ou Prioritário (true)
        async selecionarPrioridade(ehPrioritario) {
            if (this.carregando) {
                return;
            }
            this.atendimentoPrioritario = ehPrioritario;
            await this.emitirSenha();
        },

        // Botão Voltar das etapas
        voltarEtapa() {
            this.mensagemDeErro = "";
            if (this.etapaAtual === "prioridade") {
                this.etapaAtual = "documentos";
            } else if (this.etapaAtual === "documentos") {
                this.tipoSelecionado = null;
                this.identificadorTipoSelecionado = "";
                this.etapaAtual = "selecao-tipo";
            }
        },

        // Controle dos Pop-ups
        abrirPopup(nomePopup) {
            this.popupAtivo = nomePopup;
        },

        fecharPopup() {
            this.popupAtivo = null;
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
                this.etapaAtual = "recibo";
            } catch (erro) {
                this.mensagemDeErro = erro.message;
            } finally {
                this.carregando = false;
            }
        },

        // Reinicia todo o fluxo para o próximo cidadão
        reiniciarTotem() {
            this.senhaEmitida = null;
            this.tipoSelecionado = null;
            this.identificadorTipoSelecionado = "";
            this.atendimentoPrioritario = false;
            this.mensagemDeSucesso = "";
            this.mensagemDeErro = "";
            this.popupAtivo = null;
            this.etapaAtual = "selecao-tipo";
        },

        // Mantido apenas caso algo externo ainda chame limparRecibo()
        limparRecibo() {
            this.reiniciarTotem();
        },


    }
}).mount("#aplicativo-emissao");