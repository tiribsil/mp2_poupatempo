const { createApp } = Vue;

createApp({
    data() {
        return {
            dadosDoPainel: {
                ultimaSenhaChamada: null,
                ultimasSenhasChamadas: []
            },
            temporizadorDeConsulta: null,
            conectado: true,
            telaPiscando: false,
            somDeChamada: new Audio("assets/som.mp3"),
            filaDeEspera: [],
            bloqueadoPorTempoMinimo: false,
            chaveUltimaChamadaRegistrada: ""
        };
    },
    mounted() {
        this.atualizarPainel();
        this.temporizadorDeConsulta = setInterval(this.atualizarPainel, 2000);
    },
    beforeUnmount() {
        if (this.temporizadorDeConsulta) {
            clearInterval(this.temporizadorDeConsulta);
        }
    },
    methods: {
        obterChave(senha) {
            if (!senha) return "";
            return (senha.numeroFormatado || "") + "_" + (senha.instanteChamada || "");
        },

        async atualizarPainel() {
            try {
                const resposta = await fetch("api/painel");
                if (!resposta.ok) {
                    throw new Error("Erro na comunicação com o servidor.");
                }
                const dados = await resposta.json();
                this.conectado = true;

                const novaChamada = dados.ultimaSenhaChamada;
                if (!novaChamada) return;

                const chaveNova = this.obterChave(novaChamada);

                if (!this.chaveUltimaChamadaRegistrada) {
                    this.chaveUltimaChamadaRegistrada = chaveNova;
                    this.dadosDoPainel = dados;
                    return;
                }

                if (chaveNova !== this.chaveUltimaChamadaRegistrada) {
                    this.chaveUltimaChamadaRegistrada = chaveNova;
                    this.filaDeEspera.push(dados);
                    this.processarProximaDaFila();
                } else if (this.filaDeEspera.length === 0) {
                    this.dadosDoPainel.ultimasSenhasChamadas = dados.ultimasSenhasChamadas;
                }
            } catch (erro) {
                console.error("Erro ao atualizar dados do painel:", erro);
                this.conectado = false;
            }
        },

        processarProximaDaFila() {
            if (this.bloqueadoPorTempoMinimo || this.filaDeEspera.length === 0) {
                return;
            }

            const proximosDados = this.filaDeEspera.shift();
            this.dadosDoPainel = proximosDados;

            this.tocarSom();
            this.piscarTela();

            this.bloqueadoPorTempoMinimo = true;
            setTimeout(() => {
                this.bloqueadoPorTempoMinimo = false;
                this.processarProximaDaFila();
            }, 5000);
        },

        tocarSom() {
            this.somDeChamada.currentTime = 0;
            this.somDeChamada.play();
        },

        piscarTela() {
            this.telaPiscando = true;
            setTimeout(() => {
                this.telaPiscando = false;
            }, 1500);
        }
    }
}).mount("#aplicativo-painel");
