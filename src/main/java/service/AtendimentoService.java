package service;

import dao.GuicheDAO;
import dao.SenhaDAO;
import dao.TipoAtendimentoDAO;
import dto.FilaGuicheDTO;
import dto.PainelDTO;
import dto.SenhaEmitidaDTO;
import model.Guiche;
import model.Senha;
import model.StatusSenha;
import model.TipoAtendimento;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AtendimentoService {

    private final SenhaDAO senhaDAO = new SenhaDAO();
    private final GuicheDAO guicheDAO = new GuicheDAO();
    private final TipoAtendimentoDAO tipoAtendimentoDAO = new TipoAtendimentoDAO();

    private final DateTimeFormatter formatadorDeDataHora = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Map<Integer, Integer> contadorPrioritariasConsecutivasPorGuiche = new ConcurrentHashMap<>();

    public SenhaEmitidaDTO emitirSenha(Integer identificadorTipoAtendimento, boolean prioritario) {
        TipoAtendimento tipo = this.tipoAtendimentoDAO.buscarPorIdentificador(identificadorTipoAtendimento);
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de atendimento não encontrado.");
        }

        int proximoNumero = this.senhaDAO.obterProximoNumeroSequencial(identificadorTipoAtendimento);
        String prefixoPrioridade = prioritario ? "P" : "";
        String numeroFormatado = String.format("%s-%s%03d", tipo.getSigla(), prefixoPrioridade, proximoNumero);
        String instanteAtual = LocalDateTime.now().format(this.formatadorDeDataHora);

        Senha novaSenha = new Senha();
        novaSenha.setNumeroFormatado(numeroFormatado);
        novaSenha.setIdentificadorTipoAtendimento(tipo.getIdentificador());
        novaSenha.setPrioritario(prioritario);
        novaSenha.setStatus(StatusSenha.AGUARDANDO);
        novaSenha.setInstanteEmissao(instanteAtual);

        Senha senhaSalva = this.senhaDAO.salvar(novaSenha);
        int pessoasAguardandoAntes = this.senhaDAO.contarPessoasAguardandoAntes(senhaSalva.getIdentificador());

        return new SenhaEmitidaDTO(
            senhaSalva.getNumeroFormatado(),
            tipo.getNome(),
            prioritario,
            pessoasAguardandoAntes,
            instanteAtual
        );
    }

    public FilaGuicheDTO obterFilaDoGuiche(Integer numeroDoGuiche) {
        Guiche guiche = this.guicheDAO.buscarPorNumero(numeroDoGuiche);
        if (guiche == null) {
            throw new IllegalArgumentException("Guichê de número " + numeroDoGuiche + " não existe.");
        }

        List<Integer> identificadoresTipos = extrairIdentificadoresDeTipos(guiche);

        FilaGuicheDTO dto = new FilaGuicheDTO();
        dto.setNumeroDoGuiche(numeroDoGuiche);
        dto.setQuantidadeDeSenhasAguardandoCompativeis(this.senhaDAO.contarSenhasAguardandoPorTipos(identificadoresTipos, null));
        dto.setQuantidadeDeSenhasPrioritariasAguardando(this.senhaDAO.contarSenhasAguardandoPorTipos(identificadoresTipos, true));
        dto.setQuantidadeDeSenhasNormaisAguardando(this.senhaDAO.contarSenhasAguardandoPorTipos(identificadoresTipos, false));
        dto.setSenhaAtualDoGuiche(this.senhaDAO.buscarSenhaAtualDoGuiche(guiche.getIdentificador()));

        return dto;
    }

    public synchronized Senha chamarProximaSenha(Integer numeroDoGuiche) {
        Guiche guiche = this.guicheDAO.buscarPorNumero(numeroDoGuiche);
        if (guiche == null) {
            throw new IllegalArgumentException("Guichê não encontrado.");
        }

        Senha senhaAtual = this.senhaDAO.buscarSenhaAtualDoGuiche(guiche.getIdentificador());
        if (senhaAtual != null) {
            throw new IllegalStateException("O guichê já possui um atendimento em andamento (Senha " + senhaAtual.getNumeroFormatado() + "). Finalize ou marque como ausente antes de chamar a próxima.");
        }

        List<Integer> identificadoresTipos = extrairIdentificadoresDeTipos(guiche);
        if (identificadoresTipos.isEmpty()) {
            throw new IllegalStateException("O guichê não possui tipos de atendimento configurados.");
        }

        int chamadasPrioritariasConsecutivas = contadorPrioritariasConsecutivasPorGuiche.getOrDefault(guiche.getIdentificador(), 0);
        Senha proximaSenha = null;

        if (chamadasPrioritariasConsecutivas >= 2) {
            proximaSenha = this.senhaDAO.buscarProximaAguardando(identificadoresTipos, false);
            if (proximaSenha != null) {
                contadorPrioritariasConsecutivasPorGuiche.put(guiche.getIdentificador(), 0);
            } else {
                proximaSenha = this.senhaDAO.buscarProximaAguardando(identificadoresTipos, true);
                if (proximaSenha != null) {
                    contadorPrioritariasConsecutivasPorGuiche.put(guiche.getIdentificador(), chamadasPrioritariasConsecutivas + 1);
                }
            }
        } else {
            proximaSenha = this.senhaDAO.buscarProximaAguardando(identificadoresTipos, true);
            if (proximaSenha != null) {
                contadorPrioritariasConsecutivasPorGuiche.put(guiche.getIdentificador(), chamadasPrioritariasConsecutivas + 1);
            } else {
                proximaSenha = this.senhaDAO.buscarProximaAguardando(identificadoresTipos, false);
                if (proximaSenha != null) {
                    contadorPrioritariasConsecutivasPorGuiche.put(guiche.getIdentificador(), 0);
                }
            }
        }

        if (proximaSenha == null) {
            return null;
        }

        String instanteChamada = LocalDateTime.now().format(this.formatadorDeDataHora);
        this.senhaDAO.atualizarParaChamada(proximaSenha.getIdentificador(), guiche.getIdentificador(), instanteChamada);

        proximaSenha.setStatus(StatusSenha.CHAMADA);
        proximaSenha.setIdentificadorGuiche(guiche.getIdentificador());
        proximaSenha.setNumeroGuiche(guiche.getNumero());
        proximaSenha.setInstanteChamada(instanteChamada);

        return proximaSenha;
    }

    public void iniciarAtendimento(Integer identificadorSenha) {
        String instanteInicio = LocalDateTime.now().format(this.formatadorDeDataHora);
        this.senhaDAO.atualizarParaInicioAtendimento(identificadorSenha, instanteInicio);
    }

    public void finalizarAtendimento(Integer identificadorSenha) {
        String instanteFim = LocalDateTime.now().format(this.formatadorDeDataHora);
        this.senhaDAO.atualizarParaFinalizada(identificadorSenha, instanteFim);
    }

    public void marcarComoAusente(Integer identificadorSenha) {
        this.senhaDAO.atualizarParaAusente(identificadorSenha);
    }

    public PainelDTO obterDadosDoPainel() {
        Senha ultimaChamada = this.senhaDAO.buscarUltimaSenhaChamada();
        List<Senha> historicoUltimas = this.senhaDAO.listarUltimasSenhasChamadas(5);
        return new PainelDTO(ultimaChamada, historicoUltimas);
    }

    private List<Integer> extrairIdentificadoresDeTipos(Guiche guiche) {
        List<Integer> lista = new ArrayList<>();
        if (guiche.getTiposDeAtendimentoAtendidos() != null) {
            for (TipoAtendimento tipo : guiche.getTiposDeAtendimentoAtendidos()) {
                lista.add(tipo.getIdentificador());
            }
        }
        return lista;
    }
}
