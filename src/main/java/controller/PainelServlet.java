package controller;

import com.google.gson.Gson;
import dto.PainelDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AtendimentoService;

import java.io.IOException;

@WebServlet("/api/painel")
public class PainelServlet extends HttpServlet {

    private final AtendimentoService atendimentoService = new AtendimentoService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta) throws IOException {
        resposta.setContentType("application/json;charset=UTF-8");

        try {
            PainelDTO painelDto = this.atendimentoService.obterDadosDoPainel();
            resposta.getWriter().write(this.gson.toJson(painelDto));
        } catch (Exception excecao) {
            resposta.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resposta.getWriter().write(this.gson.toJson(new MensagemErro("Erro ao consultar dados do painel: " + excecao.getMessage())));
        }
    }

    private static class MensagemErro {
        private final String erro;

        public MensagemErro(String erro) {
            this.erro = erro;
        }

        public String getErro() {
            return this.erro;
        }
    }
}
