package br.insper.cursos.service;

import br.insper.cursos.dto.CursoRequestDTO;
import br.insper.cursos.dto.CursoResponseDTO;
import br.insper.cursos.entity.Curso;
import br.insper.cursos.exception.CursoNaoEncontradoException;
import br.insper.cursos.repository.CursoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public List<CursoResponseDTO> listar(String nome) {
        List<Curso> cursos;
        if (nome == null || nome.isBlank()) {
            cursos = cursoRepository.findByAtivoTrue();
        } else {
            cursos = cursoRepository.findByAtivoTrueAndNomeStartingWithIgnoreCase(nome);
        }
        return cursos.stream().map(CursoResponseDTO::fromEntity).toList();
    }

    public CursoResponseDTO criar(CursoRequestDTO dto) {
        Curso curso = new Curso(dto.getNome(), dto.getDescricao());
        Curso salvo = cursoRepository.save(curso);
        return CursoResponseDTO.fromEntity(salvo);
    }

    public void excluir(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new CursoNaoEncontradoException("Curso não encontrado com o id: " + id));

        if (!curso.isAtivo()) {
            throw new CursoNaoEncontradoException("Curso já desativado com o id: " + id);
        }

        curso.desativar();
        cursoRepository.save(curso);
    }
}
