package br.insper.cursos.service;


import br.insper.cursos.entity.AuditLog;
import br.insper.cursos.entity.Task;
import br.insper.cursos.repository.AuditRepository;
import br.insper.cursos.repository.TaskRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final AuditRepository auditRepository;
    private final ApplicationEventPublisher publisher;

    public TaskService(TaskRepository taskRepository, AuditRepository auditRepository, ApplicationEventPublisher publisher) {
        this.taskRepository = taskRepository;
        this.auditRepository = auditRepository;
        this.publisher = publisher;
    }

    public record TaskEvent(String type, Task task) {}

    public Task criar(Task task) {
        Task salva = taskRepository.save(task);
        publisher.publishEvent(new TaskEvent("CREATE", salva));
        return salva;
    }

    public List<Task> listar() { return taskRepository.findAll(); }

    public Task buscarPorId(Long id) { return taskRepository.findById(id).orElse(null); }

    public boolean excluir(Long id) {
        return taskRepository.findById(id).map(task -> {
            taskRepository.delete(task);
            publisher.publishEvent(new TaskEvent("DELETE", task));
            return true;
        }).orElse(false);
    }


    @EventListener // Observer 1: Auditoria Banco
    public void escutarAuditoria(TaskEvent event) {
        auditRepository.save(new AuditLog(event.type(), event.task().getId()));
    }

    @EventListener // Observer 2: Notificação Console
    public void escutarNotificacao(TaskEvent event) {
        if ("CREATE".equals(event.type()) && "ALTA".equals(event.task().getPrioridade())) {
            System.out.println("⚠️ [ALERTA] Tarefa de alta prioridade criada: " + event.task().getTitulo());
        }
    }
}
