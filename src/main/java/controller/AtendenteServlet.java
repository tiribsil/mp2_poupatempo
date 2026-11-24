package controller;

import com.google.gson.Gson;
import dao.GuicheDAO;
import dto.FilaGuicheDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Guiche;
import model.Senha;
import service.AtendimentoService;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/atendente")
public class AtendenteServlet extends HttpServlet {

    private final AtendimentoService atendimentoService = new AtendimentoService();
    private final GuicheDAO guicheDAO = new GuicheDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta) throws IOException {
        resposta.setContentType("application/json;charset=UTF-8");

        String acao = requisicao.getParameter("acao");

        try {
            if ("guiches".equalsIgnoreCase(acao)) {
                List<Guiche> listaDeGuiches = this.guicheDAO.listarTodos();
                resposta.getWriter().write(this.gson.toJson(listaDeGuiches));
                return;
            }

            if ("fila".equalsIgnoreCase(acao)) {
                String parametroGuiche = requisicao.getParameter("numeroGuiche");
                if (parametroGuiche == null) {
                    resposta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resposta.getWriter().write(this.gson.toJson(new RespostaPadrao(false, "Número do guichê não fornecido.")));
                    return;
                }

                Integer numeroGuiche = Integer.parseInt(parametroGuiche);
                FilaGuicheDTO filaDto = this.atendimentoService.obterFilaDoGuiche(numeroGuiche);
                resposta.getWriter().write(this.gson.toJson(filaDto));
                return;
            }

            resposta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resposta.getWriter().write(this.gson.toJson(new RespostaPadrao(false, "Ação inválida.")));

        } catch (Exception excecao) {
            resposta.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resposta.getWriter().write(this.gson.toJson(new RespostaPadrao(false, excecao.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta) throws IOException {
        resposta.setContentType("application/json;charset=UTF-8");

        String acao = requisicao.getParameter("acao");

        try {
            if ("proxima".equalsIgnoreCase(acao)) {
                String parametroGuiche = requisicao.getParameter("numeroGuiche");
                if (parametroGuiche == null) {
                    resposta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resposta.getWriter().write(this.gson.toJson(new RespostaPadrao(false, "Número do guichê não fornecido.")));
                    return;
                }

                Integer numeroGuiche = Integer.parseInt(parametroGuiche);
                Senha senhaChamada = this.atendimentoService.chamarProximaSenha(numeroGuiche);

                if (senhaChamada == null) {
                    resposta.getWriter().write(this.gson.toJson(new RespostaPadrao(true, "Não há senhas aguardando atendimento para este guichê.")));
                } else {
                    resposta.getWriter().write(this.gson.toJson(senhaChamada));
                }
                return;
            }

            if ("novamente".equalsIgnoreCase(acao)) {
                String parametroGuiche = requisicao.getParameter("numeroGuiche");
                if (parametroGuiche == null) {
                    resposta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resposta.getWriter().write(this.gson.toJson(new RespostaPadrao(false, "Número do guichê não fornecido.")));
                    return;
                }

                Integer numeroGuiche = Integer.parseInt(parametroGuiche);
                Senha senhaChamada = this.atendimentoService.chamarSenhaNovamente(numeroGuiche);

                if (senhaChamada == null) {
                    resposta.getWriter().write(this.gson.toJson(new RespostaPadrao(true, "Não há senhas já chamadas para este guichê.")));
                } else {
                    resposta.getWriter().write(this.gson.toJson(senhaChamada));
                }
                return;
            }

            if ("iniciar".equalsIgnoreCase(acao)) {
                Integer identificadorSenha = Integer.parseInt(requisicao.getParameter("senhaId"));
                this.atendimentoService.iniciarAtendimento(identificadorSenha);
                resposta.getWriter().write(this.gson.toJson(new RespostaPadrao(true, "Atendimento iniciado com sucesso.")));
                return;
            }

            if ("finalizar".equalsIgnoreCase(acao)) {
                Integer identificadorSenha = Integer.parseInt(requisicao.getParameter("senhaId"));
                this.atendimentoService.finalizarAtendimento(identificadorSenha);
                resposta.getWriter().write(this.gson.toJson(new RespostaPadrao(true, "Atendimento finalizado com sucesso.")));
                return;
            }

            if ("ausente".equalsIgnoreCase(acao)) {
                Integer identificadorSenha = Integer.parseInt(requisicao.getParameter("senhaId"));
                this.atendimentoService.marcarComoAusente(identificadorSenha);
                resposta.getWriter().write(this.gson.toJson(new RespostaPadrao(true, "Usuário marcado como ausente.")));
                return;
            }

            resposta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resposta.getWriter().write(this.gson.toJson(new RespostaPadrao(false, "Ação POST desconhecida.")));

        } catch (IllegalStateException | IllegalArgumentException excecao) {
            resposta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resposta.getWriter().write(this.gson.toJson(new RespostaPadrao(false, excecao.getMessage())));
        } catch (Exception excecao) {
            resposta.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resposta.getWriter().write(this.gson.toJson(new RespostaPadrao(false, "Erro ao processar solicitação: " + excecao.getMessage())));
        }
    }

    private static class RespostaPadrao {
        private final boolean sucesso;
        private final String mensagem;

        public RespostaPadrao(boolean sucesso, String mensagem) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
        }

        public boolean isSucesso() {
            return this.sucesso;
        }

        public String getMensagem() {
            return this.mensagem;
        }
    }
}
