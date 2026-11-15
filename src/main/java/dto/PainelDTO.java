package dto;

import model.Senha;
import java.util.ArrayList;
import java.util.List;

public class PainelDTO {

    private Senha ultimaSenhaChamada;
    private List<Senha> ultimasSenhasChamadas = new ArrayList<>();

    public PainelDTO() {
    }

    public PainelDTO(Senha ultimaSenhaChamada, List<Senha> ultimasSenhasChamadas) {
        this.ultimaSenhaChamada = ultimaSenhaChamada;
        this.ultimasSenhasChamadas = ultimasSenhasChamadas;
    }

    public Senha getUltimaSenhaChamada() {
        return this.ultimaSenhaChamada;
    }

    public void setUltimaSenhaChamada(Senha ultimaSenhaChamada) {
        this.ultimaSenhaChamada = ultimaSenhaChamada;
    }

    public List<Senha> getUltimasSenhasChamadas() {
        return this.ultimasSenhasChamadas;
    }

    public void setUltimasSenhasChamadas(List<Senha> ultimasSenhasChamadas) {
        this.ultimasSenhasChamadas = ultimasSenhasChamadas;
    }
}
