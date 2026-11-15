const { createApp } = Vue;

createApp({
    data() {
        return {
            dadosDoPainel: {
                ultimaSenhaChamada: null,
                ultimasSenhasChamadas: []
            },
            temporizadorDeConsulta: null,
            conectado: true
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
                this.dadosDoPainel = dados;
                this.conectado = true;
            } catch (erro) {
                console.error("Erro ao atualizar dados do painel:", erro);
                this.conectado = false;
            }
        }
    }
}).mount("#aplicativo-painel");
