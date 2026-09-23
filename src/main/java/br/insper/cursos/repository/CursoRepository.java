package br.insper.cursos.repository;

import br.insper.cursos.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    List<Curso> findByAtivoTrue();
    List<Curso> findByAtivoTrueAndNomeStartingWithIgnoreCase(String nome);
}
