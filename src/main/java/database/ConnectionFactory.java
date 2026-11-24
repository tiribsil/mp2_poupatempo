package database;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ConnectionFactory {

    private static final String CAMINHO_DO_BANCO = resolverCaminhoDoBanco();
    private static final String URL_CONEXAO = "jdbc:sqlite:" + CAMINHO_DO_BANCO;

    private static String resolverCaminhoDoBanco() {
        String diretorioConfigurado = System.getProperty("poupatempo.banco.dir");
        if (diretorioConfigurado == null || diretorioConfigurado.isBlank()) {
            diretorioConfigurado = System.getenv("POUPATEMPO_BANCO_DIR");
        }
        if (diretorioConfigurado == null || diretorioConfigurado.isBlank()) {
            diretorioConfigurado = System.getenv("GITHUB_WORKSPACE");
        }
        if (diretorioConfigurado != null && !diretorioConfigurado.isBlank()) {
            File pasta = new File(diretorioConfigurado);
            if (pasta.exists() && pasta.isDirectory()) {
                return new File(pasta, "poupatempo.db").getAbsolutePath();
            }
        }
        File arquivoNaPastaAtual = new File("poupatempo.db");
        return arquivoNaPastaAtual.getAbsolutePath();
    }

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
        String scriptSql = lerArquivoBancoSql();
        List<String> comandos = extrairComandosSql(scriptSql);

        try (Connection conexao = DriverManager.getConnection(URL_CONEXAO);
             Statement comando = conexao.createStatement()) {

            comando.execute("PRAGMA journal_mode=WAL;");

            for (String sql : comandos) {
                comando.execute(sql);
            }

        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao inicializar o banco de dados a partir do banco.sql.", excecao);
        }
    }

    private static String lerArquivoBancoSql() {
        // Procura o banco.sql na mesma pasta do .db, na raiz atual, no workspace ou no classpath
        File pastaDoBanco = new File(CAMINHO_DO_BANCO).getParentFile();
        File[] possiveisArquivos = {
            new File(pastaDoBanco, "banco.sql"),
            new File("banco.sql"),
            new File("/workspaces/mp2_poupatempo/banco.sql")
        };

        try {
            for (File arquivo : possiveisArquivos) {
                if (arquivo.exists() && arquivo.isFile()) {
                    return Files.readString(arquivo.toPath(), StandardCharsets.UTF_8);
                }
            }

            try (InputStream stream = ConnectionFactory.class.getResourceAsStream("/banco.sql")) {
                if (stream != null) {
                    return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                }
            }
        } catch (IOException excecao) {
            throw new RuntimeException("Erro ao ler o arquivo banco.sql.", excecao);
        }

        throw new RuntimeException("Arquivo banco.sql não encontrado. Banco configurado em: " + CAMINHO_DO_BANCO);
    }

    // Separa os comandos por ';' ignorando comentários '--' e ';' dentro de textos '...'
    private static List<String> extrairComandosSql(String scriptSql) {
        List<String> comandos = new ArrayList<>();
        StringBuilder atual = new StringBuilder();
        boolean dentroAspasSimples = false;

        for (int i = 0; i < scriptSql.length(); i++) {
            char c = scriptSql.charAt(i);

            if (!dentroAspasSimples && c == '-' && i + 1 < scriptSql.length() && scriptSql.charAt(i + 1) == '-') {
                while (i < scriptSql.length() && scriptSql.charAt(i) != '\n') {
                    i++;
                }
                continue;
            }

            if (c == '\'') {
                if (dentroAspasSimples && i + 1 < scriptSql.length() && scriptSql.charAt(i + 1) == '\'') {
                    atual.append("''");
                    i++;
                    continue;
                }
                dentroAspasSimples = !dentroAspasSimples;
            }

            if (!dentroAspasSimples && c == ';') {
                String sql = atual.toString().trim();
                if (!sql.isEmpty()) {
                    comandos.add(sql);
                }
                atual.setLength(0);
            } else {
                atual.append(c);
            }
        }

        String restante = atual.toString().trim();
        if (!restante.isEmpty()) {
            comandos.add(restante);
        }

        return comandos;
    }
}