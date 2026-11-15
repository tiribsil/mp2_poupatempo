package model;

public class Senha {

    private Integer identificador;
    private String numeroFormatado;
    private Integer identificadorTipoAtendimento;
    private String nomeTipoAtendimento;
    private boolean prioritario;
    private StatusSenha status;
    private String instanteEmissao;
    private String instanteChamada;
    private String instanteInicioAtendimento;
    private String instanteFimAtendimento;
    private Integer identificadorGuiche;
    private Integer numeroGuiche;

    public Senha() {
    }

    public Integer getIdentificador() {
        return this.identificador;
    }

    public void setIdentificador(Integer identificador) {
        this.identificador = identificador;
    }

    public String getNumeroFormatado() {
        return this.numeroFormatado;
    }

    public void setNumeroFormatado(String numeroFormatado) {
        this.numeroFormatado = numeroFormatado;
    }

    public Integer getIdentificadorTipoAtendimento() {
        return this.identificadorTipoAtendimento;
    }

    public void setIdentificadorTipoAtendimento(Integer identificadorTipoAtendimento) {
        this.identificadorTipoAtendimento = identificadorTipoAtendimento;
    }

    public String getNomeTipoAtendimento() {
        return this.nomeTipoAtendimento;
    }

    public void setNomeTipoAtendimento(String nomeTipoAtendimento) {
        this.nomeTipoAtendimento = nomeTipoAtendimento;
    }

    public boolean isPrioritario() {
        return this.prioritario;
    }

    public void setPrioritario(boolean prioritario) {
        this.prioritario = prioritario;
    }

    public StatusSenha getStatus() {
        return this.status;
    }

    public void setStatus(StatusSenha status) {
        this.status = status;
    }

    public String getInstanteEmissao() {
        return this.instanteEmissao;
    }

    public void setInstanteEmissao(String instanteEmissao) {
        this.instanteEmissao = instanteEmissao;
    }

    public String getInstanteChamada() {
        return this.instanteChamada;
    }

    public void setInstanteChamada(String instanteChamada) {
        this.instanteChamada = instanteChamada;
    }

    public String getInstanteInicioAtendimento() {
        return this.instanteInicioAtendimento;
    }

    public void setInstanteInicioAtendimento(String instanteInicioAtendimento) {
        this.instanteInicioAtendimento = instanteInicioAtendimento;
    }

    public String getInstanteFimAtendimento() {
        return this.instanteFimAtendimento;
    }

    public void setInstanteFimAtendimento(String instanteFimAtendimento) {
        this.instanteFimAtendimento = instanteFimAtendimento;
    }

    public Integer getIdentificadorGuiche() {
        return this.identificadorGuiche;
    }

    public void setIdentificadorGuiche(Integer identificadorGuiche) {
        this.identificadorGuiche = identificadorGuiche;
    }

    public Integer getNumeroGuiche() {
        return this.numeroGuiche;
    }

    public void setNumeroGuiche(Integer numeroGuiche) {
        this.numeroGuiche = numeroGuiche;
    }
}
