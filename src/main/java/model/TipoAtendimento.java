package model;

public class TipoAtendimento {

    private Integer identificador;
    private String nome;
    private String sigla;

    public TipoAtendimento() {
    }

    public TipoAtendimento(Integer identificador, String nome, String sigla) {
        this.identificador = identificador;
        this.nome = nome;
        this.sigla = sigla;
    }

    public Integer getIdentificador() {
        return this.identificador;
    }

    public void setIdentificador(Integer identificador) {
        this.identificador = identificador;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return this.sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }
}
