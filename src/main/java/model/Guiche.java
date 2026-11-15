package model;

import java.util.ArrayList;
import java.util.List;

public class Guiche {

    private Integer identificador;
    private Integer numero;
    private List<TipoAtendimento> tiposDeAtendimentoAtendidos = new ArrayList<>();

    public Guiche() {
    }

    public Guiche(Integer identificador, Integer numero) {
        this.identificador = identificador;
        this.numero = numero;
    }

    public Integer getIdentificador() {
        return this.identificador;
    }

    public void setIdentificador(Integer identificador) {
        this.identificador = identificador;
    }

    public Integer getNumero() {
        return this.numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public List<TipoAtendimento> getTiposDeAtendimentoAtendidos() {
        return this.tiposDeAtendimentoAtendidos;
    }

    public void setTiposDeAtendimentoAtendidos(List<TipoAtendimento> tiposDeAtendimentoAtendidos) {
        this.tiposDeAtendimentoAtendidos = tiposDeAtendimentoAtendidos;
    }

    public boolean atendeTipo(Integer identificadorTipo) {
        if (this.tiposDeAtendimentoAtendidos == null || identificadorTipo == null) {
            return false;
        }
        for (TipoAtendimento tipo : this.tiposDeAtendimentoAtendidos) {
            if (tipo.getIdentificador().equals(identificadorTipo)) {
                return true;
            }
        }
        return false;
    }
}
