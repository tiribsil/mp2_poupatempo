package dto;

import model.Senha;

public class FilaGuicheDTO {

    private Integer numeroDoGuiche;
    private int quantidadeDeSenhasAguardandoCompativeis;
    private int quantidadeDeSenhasPrioritariasAguardando;
    private int quantidadeDeSenhasNormaisAguardando;
    private Senha senhaAtualDoGuiche;

    public FilaGuicheDTO() {
    }

    public Integer getNumeroDoGuiche() {
        return this.numeroDoGuiche;
    }

    public void setNumeroDoGuiche(Integer numeroDoGuiche) {
        this.numeroDoGuiche = numeroDoGuiche;
    }

    public int getQuantidadeDeSenhasAguardandoCompativeis() {
        return this.quantidadeDeSenhasAguardandoCompativeis;
    }

    public void setQuantidadeDeSenhasAguardandoCompativeis(int quantidadeDeSenhasAguardandoCompativeis) {
        this.quantidadeDeSenhasAguardandoCompativeis = quantidadeDeSenhasAguardandoCompativeis;
    }

    public int getQuantidadeDeSenhasPrioritariasAguardando() {
        return this.quantidadeDeSenhasPrioritariasAguardando;
    }

    public void setQuantidadeDeSenhasPrioritariasAguardando(int quantidadeDeSenhasPrioritariasAguardando) {
        this.quantidadeDeSenhasPrioritariasAguardando = quantidadeDeSenhasPrioritariasAguardando;
    }

    public int getQuantidadeDeSenhasNormaisAguardando() {
        return this.quantidadeDeSenhasNormaisAguardando;
    }

    public void setQuantidadeDeSenhasNormaisAguardando(int quantidadeDeSenhasNormaisAguardando) {
        this.quantidadeDeSenhasNormaisAguardando = quantidadeDeSenhasNormaisAguardando;
    }

    public Senha getSenhaAtualDoGuiche() {
        return this.senhaAtualDoGuiche;
    }

    public void setSenhaAtualDoGuiche(Senha senhaAtualDoGuiche) {
        this.senhaAtualDoGuiche = senhaAtualDoGuiche;
    }
}
