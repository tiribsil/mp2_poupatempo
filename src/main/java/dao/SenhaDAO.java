package dao;

import database.ConnectionFactory;
import model.Senha;
import model.StatusSenha;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SenhaDAO {

    public Senha salvar(Senha senha) {
        String comandoSql = """
            INSERT INTO senha (
                numero_formatado, tipo_atendimento_id, prioritario, status, instante_emissao
            ) VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comandoPreparado = conexao.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS)) {

            comandoPreparado.setString(1, senha.getNumeroFormatado());
            comandoPreparado.setInt(2, senha.getIdentificadorTipoAtendimento());
            comandoPreparado.setInt(3, senha.isPrioritario() ? 1 : 0);
            comandoPreparado.setString(4, senha.getStatus().name());
            comandoPreparado.setString(5, senha.getInstanteEmissao());

            comandoPreparado.executeUpdate();

            try (ResultSet chavesGeradas = comandoPreparado.getGeneratedKeys()) {
                if (chavesGeradas.next()) {
                    senha.setIdentificador(chavesGeradas.getInt(1));
                }
            }

            return senha;

        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao salvar senha no banco de dados.", excecao);
        }
    }

    public int obterProximoNumeroSequencial(Integer identificadorTipoAtendimento) {
        String comandoSql = "SELECT COUNT(*) FROM senha WHERE tipo_atendimento_id = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comandoPreparado = conexao.prepareStatement(comandoSql)) {

            comandoPreparado.setInt(1, identificadorTipoAtendimento);

            try (ResultSet resultado = comandoPreparado.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt(1) + 1;
                }
            }
            return 1;

        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao calcular próximo número sequencial da senha.", excecao);
        }
    }

    public int contarPessoasAguardandoAntes(Integer identificadorSenha) {
        String comandoSql = "SELECT COUNT(*) FROM senha WHERE status = 'AGUARDANDO' AND id < ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comandoPreparado = conexao.prepareStatement(comandoSql)) {

            comandoPreparado.setInt(1, identificadorSenha);

            try (ResultSet resultado = comandoPreparado.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt(1);
                }
            }
            return 0;

        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao contar pessoas aguardando antes da senha.", excecao);
        }
    }

    public int contarSenhasAguardandoPorTipos(List<Integer> identificadoresTipos, Boolean apenasPrioritarias) {
        if (identificadoresTipos == null || identificadoresTipos.isEmpty()) {
            return 0;
        }

        String marcadores = String.join(",", Collections.nCopies(identificadoresTipos.size(), "?"));
        StringBuilder comandoSql = new StringBuilder();
        comandoSql.append("SELECT COUNT(*) FROM senha WHERE status = 'AGUARDANDO' AND tipo_atendimento_id IN (")
                  .append(marcadores)
                  .append(")");

        if (apenasPrioritarias != null) {
            comandoSql.append(" AND prioritario = ").append(apenasPrioritarias ? "1" : "0");
        }

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comandoPreparado = conexao.prepareStatement(comandoSql.toString())) {

            for (int indice = 0; indice < identificadoresTipos.size(); indice++) {
                comandoPreparado.setInt(indice + 1, identificadoresTipos.get(indice));
            }

            try (ResultSet resultado = comandoPreparado.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt(1);
                }
            }
            return 0;

        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao contar senhas aguardando por tipos.", excecao);
        }
    }

    public Senha buscarProximaAguardando(List<Integer> identificadoresTipos, boolean apenasPrioritarias) {
        if (identificadoresTipos == null || identificadoresTipos.isEmpty()) {
            return null;
        }

        String marcadores = String.join(",", Collections.nCopies(identificadoresTipos.size(), "?"));
        String comandoSql = """
            SELECT s.id, s.numero_formatado, s.tipo_atendimento_id, ta.nome AS nome_tipo,
                   s.prioritario, s.status, s.instante_emissao, s.instante_chamada,
                   s.instante_inicio_atendimento, s.instante_fim_atendimento, s.guiche_id, g.numero AS numero_guiche
            FROM senha s
            INNER JOIN tipo_atendimento ta ON ta.id = s.tipo_atendimento_id
            LEFT JOIN guiche g ON g.id = s.guiche_id
            WHERE s.status = 'AGUARDANDO' AND s.prioritario = ? AND s.tipo_atendimento_id IN (%s)
            ORDER BY s.id ASC
            LIMIT 1
        """.formatted(marcadores);

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comandoPreparado = conexao.prepareStatement(comandoSql)) {

            comandoPreparado.setInt(1, apenasPrioritarias ? 1 : 0);
            for (int indice = 0; indice < identificadoresTipos.size(); indice++) {
                comandoPreparado.setInt(indice + 2, identificadoresTipos.get(indice));
            }

            try (ResultSet resultado = comandoPreparado.executeQuery()) {
                if (resultado.next()) {
                    return preencherSenha(resultado);
                }
            }
            return null;

        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao buscar próxima senha aguardando.", excecao);
        }
    }

    public Senha buscarSenhaAtualDoGuiche(Integer identificadorGuiche) {
        String comandoSql = """
            SELECT s.id, s.numero_formatado, s.tipo_atendimento_id, ta.nome AS nome_tipo,
                   s.prioritario, s.status, s.instante_emissao, s.instante_chamada,
                   s.instante_inicio_atendimento, s.instante_fim_atendimento, s.guiche_id, g.numero AS numero_guiche
            FROM senha s
            INNER JOIN tipo_atendimento ta ON ta.id = s.tipo_atendimento_id
            LEFT JOIN guiche g ON g.id = s.guiche_id
            WHERE s.guiche_id = ? AND (s.status = 'CHAMADA' OR s.status = 'EM_ATENDIMENTO')
            ORDER BY s.id DESC
            LIMIT 1
        """;

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comandoPreparado = conexao.prepareStatement(comandoSql)) {

            comandoPreparado.setInt(1, identificadorGuiche);

            try (ResultSet resultado = comandoPreparado.executeQuery()) {
                if (resultado.next()) {
                    return preencherSenha(resultado);
                }
            }
            return null;

        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao buscar senha atual do guichê.", excecao);
        }
    }

    public void atualizarParaChamada(Integer identificadorSenha, Integer identificadorGuiche, String instanteChamada) {
        String comandoSql = """
            UPDATE senha
            SET status = 'CHAMADA', guiche_id = ?, instante_chamada = ?
            WHERE id = ? AND status = 'AGUARDANDO'
        """;

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comandoPreparado = conexao.prepareStatement(comandoSql)) {

            comandoPreparado.setInt(1, identificadorGuiche);
            comandoPreparado.setString(2, instanteChamada);
            comandoPreparado.setInt(3, identificadorSenha);

            int linhasAfetadas = comandoPreparado.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new IllegalStateException("A senha já foi chamada ou não está mais disponível.");
            }

        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao atualizar status da senha para CHAMADA.", excecao);
        }
    }

    public void atualizarParaInicioAtendimento(Integer identificadorSenha, String instanteInicio) {
        String comandoSql = """
            UPDATE senha
            SET status = 'EM_ATENDIMENTO', instante_inicio_atendimento = ?
            WHERE id = ? AND status = 'CHAMADA'
        """;

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comandoPreparado = conexao.prepareStatement(comandoSql)) {

            comandoPreparado.setString(1, instanteInicio);
            comandoPreparado.setInt(2, identificadorSenha);

            comandoPreparado.executeUpdate();

        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao atualizar status da senha para EM_ATENDIMENTO.", excecao);
        }
    }

    public void atualizarParaFinalizada(Integer identificadorSenha, String instanteFim) {
        String comandoSql = """
            UPDATE senha
            SET status = 'FINALIZADA', instante_fim_atendimento = ?
            WHERE id = ? AND status = 'EM_ATENDIMENTO'
        """;

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comandoPreparado = conexao.prepareStatement(comandoSql)) {

            comandoPreparado.setString(1, instanteFim);
            comandoPreparado.setInt(2, identificadorSenha);

            comandoPreparado.executeUpdate();

        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao atualizar status da senha para FINALIZADA.", excecao);
        }
    }

    public void atualizarParaAusente(Integer identificadorSenha) {
        String comandoSql = """
            UPDATE senha
            SET status = 'AUSENTE'
            WHERE id = ? AND status = 'CHAMADA'
        """;

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comandoPreparado = conexao.prepareStatement(comandoSql)) {

            comandoPreparado.setInt(1, identificadorSenha);

            comandoPreparado.executeUpdate();

        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao atualizar status da senha para AUSENTE.", excecao);
        }
    }

    public Senha buscarUltimaSenhaChamada() {
        String comandoSql = """
            SELECT s.id, s.numero_formatado, s.tipo_atendimento_id, ta.nome AS nome_tipo,
                   s.prioritario, s.status, s.instante_emissao, s.instante_chamada,
                   s.instante_inicio_atendimento, s.instante_fim_atendimento, s.guiche_id, g.numero AS numero_guiche
            FROM senha s
            INNER JOIN tipo_atendimento ta ON ta.id = s.tipo_atendimento_id
            LEFT JOIN guiche g ON g.id = s.guiche_id
            WHERE s.instante_chamada IS NOT NULL
            ORDER BY s.instante_chamada DESC, s.id DESC
            LIMIT 1
        """;

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comandoPreparado = conexao.prepareStatement(comandoSql);
             ResultSet resultado = comandoPreparado.executeQuery()) {

            if (resultado.next()) {
                return preencherSenha(resultado);
            }
            return null;

        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao buscar última senha chamada.", excecao);
        }
    }

    public List<Senha> listarUltimasSenhasChamadas(int limite) {
        String comandoSql = """
            SELECT s.id, s.numero_formatado, s.tipo_atendimento_id, ta.nome AS nome_tipo,
                   s.prioritario, s.status, s.instante_emissao, s.instante_chamada,
                   s.instante_inicio_atendimento, s.instante_fim_atendimento, s.guiche_id, g.numero AS numero_guiche
            FROM senha s
            INNER JOIN tipo_atendimento ta ON ta.id = s.tipo_atendimento_id
            LEFT JOIN guiche g ON g.id = s.guiche_id
            WHERE s.instante_chamada IS NOT NULL
            ORDER BY s.instante_chamada DESC, s.id DESC
            LIMIT ?
        """;

        List<Senha> listaDeSenhas = new ArrayList<>();

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comandoPreparado = conexao.prepareStatement(comandoSql)) {

            comandoPreparado.setInt(1, limite);

            try (ResultSet resultado = comandoPreparado.executeQuery()) {
                while (resultado.next()) {
                    listaDeSenhas.add(preencherSenha(resultado));
                }
            }

            return listaDeSenhas;

        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao listar últimas senhas chamadas.", excecao);
        }
    }

    private Senha preencherSenha(ResultSet resultado) throws SQLException {
        Senha senha = new Senha();
        senha.setIdentificador(resultado.getInt("id"));
        senha.setNumeroFormatado(resultado.getString("numero_formatado"));
        senha.setIdentificadorTipoAtendimento(resultado.getInt("tipo_atendimento_id"));
        senha.setNomeTipoAtendimento(resultado.getString("nome_tipo"));
        senha.setPrioritario(resultado.getInt("prioritario") == 1);
        senha.setStatus(StatusSenha.aPartirDaDescricao(resultado.getString("status")));
        senha.setInstanteEmissao(resultado.getString("instante_emissao"));
        senha.setInstanteChamada(resultado.getString("instante_chamada"));
        senha.setInstanteInicioAtendimento(resultado.getString("instante_inicio_atendimento"));
        senha.setInstanteFimAtendimento(resultado.getString("instante_fim_atendimento"));
        senha.setIdentificadorGuiche(resultado.getObject("guiche_id") != null ? resultado.getInt("guiche_id") : null);
        senha.setNumeroGuiche(resultado.getObject("numero_guiche") != null ? resultado.getInt("numero_guiche") : null);
        return senha;
    }
}
