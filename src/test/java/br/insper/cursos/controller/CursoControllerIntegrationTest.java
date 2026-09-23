package br.insper.cursos.controller;

import br.insper.cursos.entity.Curso;
import br.insper.cursos.repository.CursoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CursoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CursoRepository cursoRepository;

    @BeforeEach
    void limparBanco() {
        cursoRepository.deleteAll();
    }

    @Test
    void deveCriarCursoComSucesso() throws Exception {
        mockMvc.perform(post("/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Java Pro\",\"descricao\":\"Spring Boot\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Java Pro"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void deveListarCursosPorPrefixoESemInativos() throws Exception {
        cursoRepository.save(new Curso("Java Pro", "Spring Boot"));
        Curso inativo = new Curso("JavaScript", "Web Frontend");
        inativo.desativar();
        cursoRepository.save(inativo);

        mockMvc.perform(get("/cursos").param("nome", "Jav"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Java Pro"));
    }

    @Test
    void deveExcluirCursoLogicamente() throws Exception {
        Curso curso = cursoRepository.save(new Curso("Docker & K8s", "DevOps"));

        mockMvc.perform(delete("/cursos/{id}", curso.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/cursos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
