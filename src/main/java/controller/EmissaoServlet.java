package controller;

import com.google.gson.Gson;
import dto.SenhaEmitidaDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.TipoAtendimento;
import service.AtendimentoService;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/emissao") // Usado pra fazer as requisições
public class EmissaoServlet extends HttpServlet {

    private final AtendimentoService atendimentoService = new AtendimentoService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta) throws IOException {
        resposta.setContentType("application/json;charset=UTF-8");

        List<TipoAtendimento> listaDeTipos = this.atendimentoService.listarTiposDeAtendimento();
        resposta.getWriter().write(this.gson.toJson(listaDeTipos));
    }

    @Override
    protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta) throws IOException {
        resposta.setContentType("application/json;charset=UTF-8");

        try {
            String parametroTipo = requisicao.getParameter("tipoAtendimentoId");
            String parametroPrioridade = requisicao.getParameter("prioritario");

            if (parametroTipo == null) {
                resposta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resposta.getWriter().write(this.gson.toJson(new MensagemErro("O tipo de atendimento é obrigatório.")));
                return;
            }

            Integer identificadorTipo = Integer.parseInt(parametroTipo);
            boolean prioritario = Boolean.parseBoolean(parametroPrioridade);

            SenhaEmitidaDTO senhaEmitida = this.atendimentoService.emitirSenha(identificadorTipo, prioritario);
            resposta.getWriter().write(this.gson.toJson(senhaEmitida));

        } catch (IllegalArgumentException excecao) {
            resposta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resposta.getWriter().write(this.gson.toJson(new MensagemErro(excecao.getMessage())));
        } catch (Exception excecao) {
            resposta.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resposta.getWriter().write(this.gson.toJson(new MensagemErro("Erro interno ao emitir senha: " + excecao.getMessage())));
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
