package br.insper.cursos.dto;

import br.insper.cursos.entity.Curso;

public class CursoResponseDTO {

    private Long id;
    private String nome;
    private String descricao;
    private boolean ativo;

    public CursoResponseDTO() {}

    public CursoResponseDTO(Long id, String nome, String descricao, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.ativo = ativo;
    }

    public static CursoResponseDTO fromEntity(Curso curso) {
        return new CursoResponseDTO(
                curso.getId(),
                curso.getNome(),
                curso.getDescricao(),
                curso.isAtivo()
        );
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
