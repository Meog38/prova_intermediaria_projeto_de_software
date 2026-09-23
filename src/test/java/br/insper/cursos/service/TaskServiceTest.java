package br.insper.cursos.service;

import br.insper.cursos.entity.Task;
import br.insper.cursos.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private br.insper.cursos.repository.AuditRepository auditRepository;
    @Mock private ApplicationEventPublisher publisher;

    @InjectMocks private TaskService taskService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void testeCriarComPrioridadeAlta() {
        Task task = new Task();
        task.setPrioridade("ALTA");
        task.setTitulo("Teste Alta");
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.criar(task);
        assertNotNull(result);

        TaskService.TaskEvent event = new TaskService.TaskEvent("CREATE", task);
        taskService.escutarAuditoria(event);
        taskService.escutarNotificacao(event);
    }

    @Test
    void testeCriarComPrioridadeBaixa() {
        Task task = new Task();
        task.setPrioridade("BAIXA");
        task.setTitulo("Teste Baixa");
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.criar(task);
        assertNotNull(result);

        TaskService.TaskEvent event = new TaskService.TaskEvent("CREATE", task);
        taskService.escutarNotificacao(event); // Cobre o 'if' falso da notificação
    }

    @Test
    void testeCriarOperacaoDiferente() {
        Task task = new Task();
        task.setPrioridade("ALTA");
        TaskService.TaskEvent event = new TaskService.TaskEvent("DELETE", task);
        taskService.escutarNotificacao(event); // Cobre o 'if' falso do tipo de operação
    }

    @Test
    void testeListarEBuscar() {
        when(taskRepository.findAll()).thenReturn(List.of(new Task()));
        assertFalse(taskService.listar().isEmpty());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(new Task()));
        assertNotNull(taskService.buscarPorId(1L));

        when(taskRepository.findById(2L)).thenReturn(Optional.empty());
        assertNull(taskService.buscarPorId(2L));
    }

    @Test
    void testeExcluirComSucesso() {
        Task task = new Task();
        task.setId(1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        assertTrue(taskService.excluir(1L));
    }

    @Test
    void testeExcluirInexistente() {
        when(taskRepository.findById(2L)).thenReturn(Optional.empty());
        assertFalse(taskService.excluir(2L)); // Cobre a ramificação do orElse(false)
    }
}
