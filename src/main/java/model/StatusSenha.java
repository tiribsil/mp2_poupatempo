package model;

public enum StatusSenha {
    AGUARDANDO("AGUARDANDO"),
    CHAMADA("CHAMADA"),
    EM_ATENDIMENTO("EM ATENDIMENTO"),
    FINALIZADA("FINALIZADA"),
    AUSENTE("AUSENTE");

    private final String descricao;

    StatusSenha(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public static StatusSenha aPartirDaDescricao(String texto) {
        for (StatusSenha status : values()) {
            if (status.name().equalsIgnoreCase(texto) || status.getDescricao().equalsIgnoreCase(texto)) {
                return status;
            }
        }
        return AGUARDANDO;
    }
}
