package br.com.fiap.microservico_esg.bdd.support;

import io.cucumber.spring.ScenarioScope;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

/** Facilita chamadas reais à API, com dados isolados por cenário e limpeza via LimpezaHooks. */
@Component
@ScenarioScope
public class ApiTeste {

    @LocalServerPort
    private int porta;

    @Autowired
    private CenarioContext contexto;

    public Response chamar(Method metodo, String caminho, boolean autenticado, Object corpo) {
        RequestSpecification requisicao = given().port(porta).accept(ContentType.JSON);
        if (autenticado) {
            String token = contexto.getToken();
            assertNotNull(token, "Token não foi preparado para este cenário");
            requisicao.auth().oauth2(token);
        }
        if (corpo != null) {
            requisicao.contentType(ContentType.JSON).body(corpo);
        }
        Response resposta = requisicao.when().request(metodo, caminho);
        contexto.setResposta(resposta);
        return resposta;
    }

    private Long registrar(Response resposta, java.util.List<Long> ids, String recurso) {
        assertEquals(201, resposta.statusCode(), "Falha ao preparar " + recurso + ": " + resposta.asString());
        Long id = resposta.jsonPath().getLong("id");
        assertNotNull(id, "API não retornou ID ao preparar " + recurso);
        ids.add(id);
        return id;
    }

    public Long criarUsuario() {
        String email = "bdd-" + UUID.randomUUID() + "@example.com";
        String senha = "SenhaTeste123!";
        contexto.setEmail(email);
        contexto.setSenha(senha);
        Response resposta = chamar(Method.POST, "/usuarios", false, Map.of(
                "nome", "Usuario Integracao BDD",
                "email", email,
                "senha", senha));
        return registrar(resposta, contexto.getUsuariosCriados(), "usuário");
    }

    public void autenticar() {
        if (contexto.getToken() != null) return;
        if (contexto.getEmail() == null) criarUsuario();
        Response resposta = chamar(Method.POST, "/auth/login", false, Map.of(
                "email", contexto.getEmail(),
                "senha", contexto.getSenha()));
        assertEquals(200, resposta.statusCode(), "Falha no login preparatório: " + resposta.asString());
        String token = resposta.jsonPath().getString("token");
        assertNotNull(token, "Login preparatório não retornou token");
        assertFalse(token.isBlank(), "Token preparatório vazio");
        contexto.setToken(token);
    }

    public Long criarPonto() {
        autenticar();
        Response resposta = chamar(Method.POST, "/pontos-coleta", true, dadosPonto());
        return registrar(resposta, contexto.getPontosColetaCriados(), "ponto de coleta");
    }

    public Map<String, Object> dadosPonto() {
        return Map.of(
                "nome", "Ecoponto BDD",
                "endereco", "Rua das Flores, 123",
                "cidade", "Sao Paulo",
                "estado", "SP",
                "tipoResiduoAceito", "PLASTICO",
                "ativo", 1);
    }

    public Long criarDescarte() {
        autenticar();
        if (contexto.getPontosColetaCriados().isEmpty()) criarPonto();
        Response resposta = chamar(Method.POST, "/descartes", true, dadosDescarte());
        return registrar(resposta, contexto.getDescartesCriados(), "descarte");
    }

    public Map<String, Object> dadosDescarte() {
        return Map.of(
                "usuario", Map.of("id", usuarioId()),
                "pontoColeta", Map.of("id", pontoId()),
                "tipoResiduo", "PLASTICO",
                "pesoKg", 2.50,
                "observacao", "Descarte de teste BDD");
    }

    public Long usuarioId() {
        assertFalse(contexto.getUsuariosCriados().isEmpty(), "Nenhum usuário foi preparado");
        return contexto.getUsuariosCriados().get(0);
    }

    public Long pontoId() {
        assertFalse(contexto.getPontosColetaCriados().isEmpty(), "Nenhum ponto foi preparado");
        return contexto.getPontosColetaCriados().get(0);
    }

    public Long descarteId() {
        assertFalse(contexto.getDescartesCriados().isEmpty(), "Nenhum descarte foi preparado");
        return contexto.getDescartesCriados().get(0);
    }

    public void exigirItemNaLista(Long id) {
        java.util.List<Map<String, Object>> itens = contexto.getResposta().jsonPath().getList("$");
        assertNotNull(itens, "A resposta não contém uma lista JSON");
        assertTrue(itens.stream().anyMatch(item ->
                        item.get("id") instanceof Number numero && numero.longValue() == id),
                "ID " + id + " não foi encontrado na lista retornada");
    }
}
