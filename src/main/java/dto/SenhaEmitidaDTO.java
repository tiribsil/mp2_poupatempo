package dto;

public class SenhaEmitidaDTO {

    private String numeroDaSenha;
    private String tipoDeAtendimento;
    private boolean atendimentoPrioritario;
    private int quantidadeDePessoasAguardandoAntes;
    private String instanteDeEmissao;

    public SenhaEmitidaDTO() {
    }

    public SenhaEmitidaDTO(String numeroDaSenha, String tipoDeAtendimento, boolean atendimentoPrioritario, int quantidadeDePessoasAguardandoAntes, String instanteDeEmissao) {
        this.numeroDaSenha = numeroDaSenha;
        this.tipoDeAtendimento = tipoDeAtendimento;
        this.atendimentoPrioritario = atendimentoPrioritario;
        this.quantidadeDePessoasAguardandoAntes = quantidadeDePessoasAguardandoAntes;
        this.instanteDeEmissao = instanteDeEmissao;
    }

    public String getNumeroDaSenha() {
        return this.numeroDaSenha;
    }

    public void setNumeroDaSenha(String numeroDaSenha) {
        this.numeroDaSenha = numeroDaSenha;
    }

    public String getTipoDeAtendimento() {
        return this.tipoDeAtendimento;
    }

    public void setTipoDeAtendimento(String tipoDeAtendimento) {
        this.tipoDeAtendimento = tipoDeAtendimento;
    }

    public boolean isAtendimentoPrioritario() {
        return this.atendimentoPrioritario;
    }

    public void setAtendimentoPrioritario(boolean atendimentoPrioritario) {
        this.atendimentoPrioritario = atendimentoPrioritario;
    }

    public int getQuantidadeDePessoasAguardandoAntes() {
        return this.quantidadeDePessoasAguardandoAntes;
    }

    public void setQuantidadeDePessoasAguardandoAntes(int quantidadeDePessoasAguardandoAntes) {
        this.quantidadeDePessoasAguardandoAntes = quantidadeDePessoasAguardandoAntes;
    }

    public String getInstanteDeEmissao() {
        return this.instanteDeEmissao;
    }

    public void setInstanteDeEmissao(String instanteDeEmissao) {
        this.instanteDeEmissao = instanteDeEmissao;
    }
}
