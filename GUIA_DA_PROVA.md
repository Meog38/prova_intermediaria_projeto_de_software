# 🚀 Guia Definitivo da Prova de Projeto de Software

Este guia foi elaborado a partir de todas as exigências do curso (SOLID, Qualidade, Docker, CI/CD, JaCoCo, AWS e GitHub Actions) para você ter **segurança total** e não travar em nenhum detalhe técnico durante a prova.

---

## ⚡ Sumário Rápido

1. [Status Atual do Repositório](#1-status-atual-do-repositório)
2. [Alerta do Docker Desktop Local](#2-alerta-do-docker-desktop-local)
3. [Tabela de Secrets do GitHub](#3-tabela-de-secrets-do-github)
4. [Roteiro Passo a Passo na Prova](#4-roteiro-passo-a-passo-na-prova)
5. [Como Adaptar para Outro Domínio em 5 Minutos](#5-como-adaptar-para-outro-domínio-em-5-minutos)
6. [Fluxo Obrigatório de Pull Request (PR)](#6-fluxo-obrigatório-de-pull-request-pr)
7. [Troubleshooting & Comandos de Emergência](#7-troubleshooting--comandos-de-emergência)

---

## 1. Status Atual do Repositório

O repositório já está configurado com:
- ✅ **Spring Boot + Java 21 LTS** e Maven.
- ✅ **Banco em Memória H2** configurado para a suíte de testes (`src/test/resources/application.properties`). Isso garante que os testes no GitHub Actions e no seu PC rodem em segundos **sem depender de banco externo**.
- ✅ **Banco PostgreSQL** configurado para execução real (`src/main/resources/application.properties`) usando variáveis de ambiente (`SPRING_DATASOURCE_*` / `DB_*`).
- ✅ **JaCoCo** configurado no `pom.xml` para travar qualquer build no `mvn verify` que tiver menos de **100% de cobertura de linhas** no pacote de `service`.
- ✅ **Testes Unitários (Mockito)** com 100% de cobertura nos métodos da Service.
- ✅ **Testes de Integração (MockMvc + SpringBootTest)** testando as rotas HTTP de ponta a ponta.
- ✅ **Dockerfile multi-stage** (compila com Maven Temurin 21 e roda em imagem JRE leve).
- ✅ **Workflows do GitHub Actions**:
  - `.github/workflows/test.yml`: Dispara em **Pull Requests** para `main`, executa `mvn -B verify` e mostra o selo verde de cobertura aprovada no PR.
  - `.github/workflows/deploy.yml`: Dispara em **Push** na `main`, compila o pacote, constrói e envia a imagem para o Docker Hub, e conecta via SSH na AWS para reiniciar o container na rede `rede`.

---

## 2. Alerta do Docker Desktop Local

> [!WARNING]
> No seu Docker Desktop, os contêineres do projeto `wa-finance-ai-bot` podem estar ativos usando as portas **8080** e **5432**.
> Se for testar a API ou o Postgres localmente pelo Docker, pare-os primeiro para não dar conflito de porta (`port already allocated`):

```powershell
# No PowerShell ou Git Bash:
docker stop wa-finance-ai-bot-evolution-api-1 wa-finance-ai-bot-postgres-1
```

Para subir a rede e o Postgres local (se quiser testar via Docker no seu PC):
```powershell
docker network create -d bridge rede
docker run -d --name postgres-simulado -e POSTGRES_DB=simuladodb -e POSTGRES_USER=usuario -e POSTGRES_PASSWORD=senha --network rede -p 5432:5432 postgres
```

---

## 3. Tabela de Secrets do GitHub

No repositório do GitHub, vá em:
`Settings` ➔ `Secrets and variables` ➔ `Actions` ➔ `New repository secret`

Cadastre as seguintes chaves com os valores correspondentes:

| Secret | O que colocar | Exemplo |
| :--- | :--- | :--- |
| `DOCKERHUB_USERNAME` | Seu usuário do Docker Hub | `ot0mz` (ou seu login) |
| `DOCKERHUB_TOKEN` | Access Token gerado no Docker Hub | `dckr_pat_...` |
| `HOST_TEST` | IP público da máquina AWS | `3.221.155.116` (ou fornecido pelo professor) |
| `KEY_TEST` | Conteúdo **inteiro** do arquivo `.pem` | Copiar do `-----BEGIN RSA...` até `-----END...` |
| `DB_HOST` | Host do banco Postgres na rede Docker | `postgres-aula` (ou `postgres-simulado`) |
| `DB_PORT` | Porta do banco | `5432` |
| `DB_NAME` | Nome da base de dados | `auladb` (ou `simuladodb`) |
| `DB_USER` | Usuário do banco | `usuario` |
| `DB_PASSWORD` | Senha do banco | `senha` |

> [!TIP]
> Cadastre as Secrets **antes** de fazer o primeiro push na `main`, para o workflow de deploy não falhar na primeira execução.

---

## 4. Roteiro Passo a Passo na Prova

### Passo 0: Checagem Inicial
1. Abra o Docker Desktop no seu PC.
2. Certifique-se de que a máquina AWS está acessível (se tiver a chave `.pem`):
   ```bash
   ssh -i projsoft26b.pem ubuntu@<IP_DA_AWS>
   docker ps
   exit
   ```

### Passo 1: Ler o Enunciado
Anote com atenção:
- Nome da entidade e da rota (ex: `/produtos`, `/tarefas`, `/veiculos`).
- Campos obrigatórios e regras de validação (`@NotBlank`, `@Positive`, etc.).
- Filtro do GET (ex: busca por prefixo `nomeStartingWithIgnoreCase`).
- Se a exclusão é lógica (*soft delete*, mantendo o registro no banco com flag booleana `ativo=false` ou `deletado=true`).
- **Qual rota deve ser entregue via Pull Request** (geralmente é o `DELETE`).

---

## 5. Como Adaptar para Outro Domínio em 5 Minutos

Se a prova pedir outro domínio (ex: `Produto` em vez de `Curso`), você pode:
1. **Opção Super Rápida**: Manter o nome interno das classes e só trocar a anotação da rota no Controller (`@RequestMapping("/produtos")`) e os campos da Entity/DTO.
2. **Opção Completa (Refatorar)**:
   - `entity/`: Ajuste campos da classe (sempre mantendo `id` e a flag booleana `ativo`/`deletado`).
   - `dto/`: Crie o RequestDTO (com validações) e ResponseDTO (sem expor campos internos desnecessários).
   - `repository/`: Ajuste os nomes dos métodos do Spring Data (ex: `findByAtivoTrueAndNomeStartingWithIgnoreCase`).
   - `service/`: Ajuste a regra de negócio e lance `CursoNaoEncontradoException` quando o registro não existir ou já estiver inativo.
   - `controller/`: Ajuste as anotações `@GetMapping`, `@PostMapping` e `@DeleteMapping`.
   - `test/`:
     - No teste unitário da Service (`CursoServiceTest`), garanta que todos os `if/else` e lançamentos de exceção sejam testados com Mockito.
     - Execute no terminal:
       ```bash
       mvn clean verify
       ```
     - Se o JaCoCo acusar cobertura menor que 100%, veja no relatório `target/site/jacoco/index.html` qual linha faltou cobrir e adicione o teste correspondente.

---

## 6. Fluxo Obrigatório de Pull Request (PR)

A prova costuma pontuar a criação de uma rota via branch e PR com verificação de cobertura automática.

### 1. No primeiro envio para a `main`:
Envie o esqueleto com os métodos base (ex: GET e POST), deixando a rota do PR de fora:
```bash
git add .
git commit -m "feat: API base com listagem e criacao + CI/CD"
git push origin main
```
Acompanhe na aba **Actions** do GitHub: o deploy irá rodar e subir a imagem na AWS.

### 2. Implementar a rota do PR (ex: DELETE):
```bash
# Cria uma nova branch
git checkout -b feature/delete-item

# Implemente o método de exclusão no Controller, Service e os testes correspondentes.
# Rode os testes locais com JaCoCo:
mvn clean verify

# Se der BUILD SUCCESS:
git add .
git commit -m "feat: implementa exclusao logica com testes"
git push -u origin feature/delete-item
```

### 3. Abrir e Aprovar o PR no GitHub:
1. Vá até o repositório no GitHub.
2. Clique em **Compare & pull request**.
3. Veja o check **Test and Coverage** rodando automaticamente.
4. Quando o check ficar **verde** (comprovando 100% de cobertura nos testes), clique em **Merge pull request** ➔ **Confirm merge**.
5. **NÃO apague a branch** se o professor pedir para mantê-la.
6. No terminal do seu computador, atualize a `main`:
   ```bash
   git checkout main
   git pull
   ```

---

## 7. Troubleshooting & Comandos de Emergência

### ❌ Erro: "Port already allocated" na AWS ou no PC
A porta 8080 já está em uso por outro container.
**Solução**:
```bash
docker ps
docker stop <nome_do_container_antigo>
docker rm <nome_do_container_antigo>
```

### ❌ Erro: Testes falham no GitHub Actions por conexão no Postgres
**Causa**: O teste de integração tentou conectar no Postgres da AWS em vez de usar H2.
**Solução**: Certifique-se de que o arquivo `src/test/resources/application.properties` existe e contém a URL `jdbc:h2:mem:...`. O Spring Boot carrega essa configuração automaticamente para testes.

### ❌ Erro: JaCoCo falha no build (`Coverage checks have not been met`)
**Causa**: Alguma linha ou branch do seu Service não foi executada no teste unitário.
**Solução**: Abra `target/site/jacoco/index.html` no navegador, localize a linha em vermelho ou amarelo e crie um `@Test` cobrindo exatamente aquele cenário (por exemplo, quando o ID não existe ou o nome é nulo/vazio).

### 🔍 Comandos úteis na AWS:
```bash
# Conectar na maquina
ssh -i projsoft26b.pem ubuntu@<IP_DA_AWS>

# Ver contêineres ativos
docker ps

# Ver todos os contêineres (incluindo os que falharam)
docker ps -a

# Ver logs da aplicacao em tempo real
docker logs -f cursos

# Deslogar da AWS
exit
```
