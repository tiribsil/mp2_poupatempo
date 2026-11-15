package dao;

import database.ConnectionFactory;
import model.Guiche;
import model.TipoAtendimento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GuicheDAO {

    public List<Guiche> listarTodos() {
        String comandoSql = "SELECT id, numero FROM guiche ORDER BY numero";
        List<Guiche> listaDeGuiches = new ArrayList<>();

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comandoPreparado = conexao.prepareStatement(comandoSql);
             ResultSet resultadoConsulta = comandoPreparado.executeQuery()) {

            while (resultadoConsulta.next()) {
                Guiche guiche = new Guiche(
                    resultadoConsulta.getInt("id"),
                    resultadoConsulta.getInt("numero")
                );
                guiche.setTiposDeAtendimentoAtendidos(buscarTiposDoGuiche(conexao, guiche.getIdentificador()));
                listaDeGuiches.add(guiche);
            }

            return listaDeGuiches;

        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao consultar guichês no banco de dados.", excecao);
        }
    }

    public Guiche buscarPorNumero(Integer numeroDoGuiche) {
        String comandoSql = "SELECT id, numero FROM guiche WHERE numero = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comandoPreparado = conexao.prepareStatement(comandoSql)) {

            comandoPreparado.setInt(1, numeroDoGuiche);

            try (ResultSet resultadoConsulta = comandoPreparado.executeQuery()) {
                if (resultadoConsulta.next()) {
                    Guiche guiche = new Guiche(
                        resultadoConsulta.getInt("id"),
                        resultadoConsulta.getInt("numero")
                    );
                    guiche.setTiposDeAtendimentoAtendidos(buscarTiposDoGuiche(conexao, guiche.getIdentificador()));
                    return guiche;
                }
            }

            return null;

        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao buscar guichê por número.", excecao);
        }
    }

    private List<TipoAtendimento> buscarTiposDoGuiche(Connection conexao, Integer identificadorGuiche) throws SQLException {
        String comandoSql = """
            SELECT ta.id, ta.nome, ta.sigla
            FROM tipo_atendimento ta
            INNER JOIN guiche_tipo_atendimento gta ON gta.tipo_atendimento_id = ta.id
            WHERE gta.guiche_id = ?
            ORDER BY ta.id
        """;

        List<TipoAtendimento> tipos = new ArrayList<>();
        try (PreparedStatement comandoPreparado = conexao.prepareStatement(comandoSql)) {
            comandoPreparado.setInt(1, identificadorGuiche);
            try (ResultSet resultado = comandoPreparado.executeQuery()) {
                while (resultado.next()) {
                    tipos.add(new TipoAtendimento(
                        resultado.getInt("id"),
                        resultado.getString("nome"),
                        resultado.getString("sigla")
                    ));
                }
            }
        }
        return tipos;
    }
}
