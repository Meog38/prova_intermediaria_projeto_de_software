package br.insper.cursos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tipoOperacao; // CREATE ou DELETE
    private LocalDateTime timestamp = LocalDateTime.now();
    private Long taskId;

    public AuditLog() {}
    public AuditLog(String tipoOperacao, Long taskId) {
        this.tipoOperacao = tipoOperacao;
        this.taskId = taskId;
    }
    // Getters
    public Long getId() { return id; }
    public String getTipoOperacao() { return tipoOperacao; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Long getTaskId() { return taskId; }
}

