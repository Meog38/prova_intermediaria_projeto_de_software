package br.insper.cursos.dto;

import jakarta.validation.constraints.NotBlank;

public class CursoRequestDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    private String descricao;

    public CursoRequestDTO() {}

    public CursoRequestDTO(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
