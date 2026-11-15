package dao;

import database.ConnectionFactory;
import model.TipoAtendimento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoAtendimentoDAO {

    public List<TipoAtendimento> listarTodos() {
        String comandoSql = "SELECT id, nome, sigla FROM tipo_atendimento ORDER BY id";
        List<TipoAtendimento> listaDeTipos = new ArrayList<>();

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comandoPreparado = conexao.prepareStatement(comandoSql);
             ResultSet resultadoConsulta = comandoPreparado.executeQuery()) {

            while (resultadoConsulta.next()) {
                TipoAtendimento tipo = new TipoAtendimento(
                    resultadoConsulta.getInt("id"),
                    resultadoConsulta.getString("nome"),
                    resultadoConsulta.getString("sigla")
                );
                listaDeTipos.add(tipo);
            }

            return listaDeTipos;

        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao consultar tipos de atendimento no banco de dados.", excecao);
        }
    }

    public TipoAtendimento buscarPorIdentificador(Integer identificador) {
        String comandoSql = "SELECT id, nome, sigla FROM tipo_atendimento WHERE id = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement comandoPreparado = conexao.prepareStatement(comandoSql)) {

            comandoPreparado.setInt(1, identificador);

            try (ResultSet resultadoConsulta = comandoPreparado.executeQuery()) {
                if (resultadoConsulta.next()) {
                    return new TipoAtendimento(
                        resultadoConsulta.getInt("id"),
                        resultadoConsulta.getString("nome"),
                        resultadoConsulta.getString("sigla")
                    );
                }
            }

            return null;

        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao buscar tipo de atendimento por identificador.", excecao);
        }
    }
}
