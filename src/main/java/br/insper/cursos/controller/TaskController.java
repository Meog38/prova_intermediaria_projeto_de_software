package br.insper.cursos.controller;

import br.insper.cursos.entity.Task;
import br.insper.cursos.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<Task> criar(@RequestBody Task task) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(task));
    }

    @GetMapping
    public List<Task> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public ResponseEntity<Task> buscar(@PathVariable Long id) {
        Task t = service.buscarPorId(id);
        return t != null ? ResponseEntity.ok(t) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        return service.excluir(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}

