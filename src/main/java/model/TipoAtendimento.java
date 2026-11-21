package model;

import java.util.ArrayList;

public class TipoAtendimento {

    private Integer identificador;
    private String nome;
    private String sigla;
    private ArrayList<String> documentos;

    public TipoAtendimento() {
        this.documentos = new ArrayList<>();
    }

    public TipoAtendimento(Integer identificador, String nome, String sigla, String documentos) {
        this.identificador = identificador;
        this.nome = nome;
        this.sigla = sigla;
        this.documentos = transformaDocsEmArray(documentos);
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

    private ArrayList<String> transformaDocsEmArray(String documentos) {
        ArrayList<String> listaDocs = new ArrayList<>();

        if (documentos == null || documentos.trim().isEmpty()) {
            return listaDocs;
        }

        String[] docsSeparados = documentos.split(",");

        for (String doc : docsSeparados) {
            String docLimpo = doc.trim();
            if (!docLimpo.isEmpty()) {
                listaDocs.add(docLimpo);
            }
        }

        return listaDocs;
    }

    public ArrayList<String> getDocumentos() {
        return this.documentos;
    }

    public void setDocumentos(String documentos) {
        this.documentos = transformaDocsEmArray(documentos); 
    }
}
