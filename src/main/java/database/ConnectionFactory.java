package database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionFactory {

    private static final String CAMINHO_DO_BANCO = System.getProperty("user.home") + File.separator + "poupatempo.db";
    private static final String URL_CONEXAO = "jdbc:sqlite:" + CAMINHO_DO_BANCO;

    // Executa no começo
    static {
        try {
            Class.forName("org.sqlite.JDBC");
            inicializarBancoDeDados();
        } catch (ClassNotFoundException excecao) {
            throw new RuntimeException("Driver do SQLite não encontrado.", excecao);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL_CONEXAO);
    }

    private static void inicializarBancoDeDados() {
        try (Connection conexao = DriverManager.getConnection(URL_CONEXAO);
             Statement comando = conexao.createStatement()) {

            comando.execute("PRAGMA journal_mode=WAL;");

            comando.execute("""
                CREATE TABLE IF NOT EXISTS tipo_atendimento (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome VARCHAR(100) NOT NULL,
                    sigla VARCHAR(10) NOT NULL
                );
            """);

            comando.execute("""
                CREATE TABLE IF NOT EXISTS guiche (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    numero INTEGER NOT NULL
                );
            """);

            comando.execute("""
                CREATE TABLE IF NOT EXISTS guiche_tipo_atendimento (
                    guiche_id INTEGER NOT NULL,
                    tipo_atendimento_id INTEGER NOT NULL,
                    PRIMARY KEY (guiche_id, tipo_atendimento_id)
                );
            """);

            comando.execute("""
                CREATE TABLE IF NOT EXISTS senha (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    numero_formatado VARCHAR(20) NOT NULL,
                    tipo_atendimento_id INTEGER NOT NULL,
                    prioritario INTEGER NOT NULL,
                    status VARCHAR(30) NOT NULL,
                    instante_emissao VARCHAR(30) NOT NULL,
                    instante_chamada VARCHAR(30),
                    instante_inicio_atendimento VARCHAR(30),
                    instante_fim_atendimento VARCHAR(30),
                    guiche_id INTEGER
                );
            """);

            // Carga inicial se estiver vazio
            var resultadoContagem = comando.executeQuery("SELECT COUNT(*) FROM tipo_atendimento");
            if (resultadoContagem.next() && resultadoContagem.getInt(1) == 0) {
                comando.execute("INSERT INTO tipo_atendimento (id, nome, sigla) VALUES (1, 'RG - Carteira de Identidade', 'RG');");
                comando.execute("INSERT INTO tipo_atendimento (id, nome, sigla) VALUES (2, 'CNH - Carteira de Motorista', 'CNH');");
                comando.execute("INSERT INTO tipo_atendimento (id, nome, sigla) VALUES (3, 'Veículos - Licenciamento', 'VEI');");

                comando.execute("INSERT INTO guiche (id, numero) VALUES (1, 1);");
                comando.execute("INSERT INTO guiche (id, numero) VALUES (2, 2);");
                comando.execute("INSERT INTO guiche (id, numero) VALUES (3, 3);");

                comando.execute("INSERT INTO guiche_tipo_atendimento (guiche_id, tipo_atendimento_id) VALUES (1, 1);");
                comando.execute("INSERT INTO guiche_tipo_atendimento (guiche_id, tipo_atendimento_id) VALUES (1, 2);");

                comando.execute("INSERT INTO guiche_tipo_atendimento (guiche_id, tipo_atendimento_id) VALUES (2, 1);");
                comando.execute("INSERT INTO guiche_tipo_atendimento (guiche_id, tipo_atendimento_id) VALUES (2, 2);");
                comando.execute("INSERT INTO guiche_tipo_atendimento (guiche_id, tipo_atendimento_id) VALUES (2, 3);");

                comando.execute("INSERT INTO guiche_tipo_atendimento (guiche_id, tipo_atendimento_id) VALUES (3, 3);");

                comando.execute("""
                    INSERT INTO senha (id, numero_formatado, tipo_atendimento_id, prioritario, status, instante_emissao, instante_chamada, instante_inicio_atendimento, instante_fim_atendimento, guiche_id)
                    VALUES (1, 'RG-001', 1, 0, 'FINALIZADA', '2026-11-15 08:30:00', '2026-11-15 08:35:00', '2026-11-15 08:36:00', '2026-11-15 08:50:00', 1);
                """);
                comando.execute("""
                    INSERT INTO senha (id, numero_formatado, tipo_atendimento_id, prioritario, status, instante_emissao, instante_chamada, instante_inicio_atendimento, instante_fim_atendimento, guiche_id)
                    VALUES (2, 'CNH-P001', 2, 1, 'FINALIZADA', '2026-11-15 08:40:00', '2026-11-15 08:45:00', '2026-11-15 08:46:00', '2026-11-15 09:05:00', 2);
                """);
                comando.execute("""
                    INSERT INTO senha (id, numero_formatado, tipo_atendimento_id, prioritario, status, instante_emissao, instante_chamada, instante_inicio_atendimento, instante_fim_atendimento, guiche_id)
                    VALUES (3, 'VEI-001', 3, 0, 'AGUARDANDO', '2026-11-15 09:10:00', NULL, NULL, NULL, NULL);
                """);
                comando.execute("""
                    INSERT INTO senha (id, numero_formatado, tipo_atendimento_id, prioritario, status, instante_emissao, instante_chamada, instante_inicio_atendimento, instante_fim_atendimento, guiche_id)
                    VALUES (4, 'RG-P002', 1, 1, 'AGUARDANDO', '2026-11-15 09:15:00', NULL, NULL, NULL, NULL);
                """);
                comando.execute("""
                    INSERT INTO senha (id, numero_formatado, tipo_atendimento_id, prioritario, status, instante_emissao, instante_chamada, instante_inicio_atendimento, instante_fim_atendimento, guiche_id)
                    VALUES (5, 'CNH-002', 2, 0, 'AGUARDANDO', '2026-11-15 09:20:00', NULL, NULL, NULL, NULL);
                """);
            }

        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao inicializar o banco de dados SQLite.", excecao);
        }
    }
}
