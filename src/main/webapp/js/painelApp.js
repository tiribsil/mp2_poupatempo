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
            somDeChamada: new Audio("som.mp3")
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
        async atualizarPainel() {
            try {
                const resposta = await fetch("api/painel");
                if (!resposta.ok) {
                    throw new Error("Erro na comunicação com o servidor.");
                }
                const dados = await resposta.json();

                const houveMudanca = this.dadosDoPainel.ultimaSenhaChamada !== null && dados.ultimaSenhaChamada !== null &&
                this.dadosDoPainel.ultimaSenhaChamada !== dados.ultimaSenhaChamada;

                this.dadosDoPainel = dados;
                this.conectado = true;

                if (houveMudanca) {
                    this.tocarSom();
                    this.piscarTela();
                }
            } catch (erro) {
                console.error("Erro ao atualizar dados do painel:", erro);
                this.conectado = false;
            }
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
