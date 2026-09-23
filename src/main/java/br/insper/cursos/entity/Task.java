package br.insper.cursos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Simplifica para ID numérico sequencial
    private Long id;
    private String titulo;
    private String descricao;
    private String status;     // "TODO", "DOING", "DONE" direto como String
    private String prioridade; // "ALTA", "MEDIA", "BAIXA" direto como String
    private LocalDateTime dataCriacao = LocalDateTime.now();

    // Getters e Setters rápidos
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}

