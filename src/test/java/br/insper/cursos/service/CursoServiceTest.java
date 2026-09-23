package br.insper.cursos.service;

import br.insper.cursos.dto.CursoRequestDTO;
import br.insper.cursos.dto.CursoResponseDTO;
import br.insper.cursos.entity.Curso;
import br.insper.cursos.exception.CursoNaoEncontradoException;
import br.insper.cursos.repository.CursoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private CursoService cursoService;

    @Test
    void deveListarTodosOsCursosQuandoNomeForNulo() {
        Curso curso = new Curso("Java Pro", "Curso Completo");
        when(cursoRepository.findByAtivoTrue()).thenReturn(List.of(curso));

        List<CursoResponseDTO> resultado = cursoService.listar(null);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Java Pro");
        verify(cursoRepository).findByAtivoTrue();
    }

    @Test
    void deveListarTodosOsCursosQuandoNomeForEmBranco() {
        Curso curso = new Curso("Python Web", "Curso Web");
        when(cursoRepository.findByAtivoTrue()).thenReturn(List.of(curso));

        List<CursoResponseDTO> resultado = cursoService.listar("   ");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Python Web");
        verify(cursoRepository).findByAtivoTrue();
    }

    @Test
    void deveListarCursosFiltrandoPorPrefixo() {
        Curso curso = new Curso("Java Pro", "Curso Completo");
        when(cursoRepository.findByAtivoTrueAndNomeStartingWithIgnoreCase("Jav")).thenReturn(List.of(curso));

        List<CursoResponseDTO> resultado = cursoService.listar("Jav");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Java Pro");
        verify(cursoRepository).findByAtivoTrueAndNomeStartingWithIgnoreCase("Jav");
    }

    @Test
    void deveCriarCursoComSucesso() {
        CursoRequestDTO dto = new CursoRequestDTO("Spring Boot", "Avançado");
        Curso salvo = new Curso("Spring Boot", "Avançado");
        when(cursoRepository.save(any(Curso.class))).thenReturn(salvo);

        CursoResponseDTO resultado = cursoService.criar(dto);

        assertThat(resultado.getNome()).isEqualTo("Spring Boot");
        assertThat(resultado.getDescricao()).isEqualTo("Avançado");
        assertThat(resultado.isAtivo()).isTrue();
        verify(cursoRepository).save(any(Curso.class));
    }

    @Test
    void deveExcluirCursoLogicamenteComSucesso() {
        Curso curso = new Curso("DevOps", "CI/CD");
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

        cursoService.excluir(1L);

        assertThat(curso.isAtivo()).isFalse();
        verify(cursoRepository).save(curso);
    }

    @Test
    void deveLancarExcecaoAoTentarExcluirCursoInexistente() {
        when(cursoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cursoService.excluir(99L))
                .isInstanceOf(CursoNaoEncontradoException.class)
                .hasMessageContaining("Curso não encontrado com o id: 99");
    }

    @Test
    void deveLancarExcecaoAoTentarExcluirCursoJaDesativado() {
        Curso curso = new Curso("Cloud", "AWS");
        curso.desativar();
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

        assertThatThrownBy(() -> cursoService.excluir(1L))
                .isInstanceOf(CursoNaoEncontradoException.class)
                .hasMessageContaining("Curso já desativado com o id: 1");
    }
}
