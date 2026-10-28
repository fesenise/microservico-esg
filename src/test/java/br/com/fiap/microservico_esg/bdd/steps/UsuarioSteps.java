package br.com.fiap.microservico_esg.bdd.steps;

import br.com.fiap.microservico_esg.bdd.support.CenarioContext;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class UsuarioSteps {

    @Autowired
    private CenarioContext contexto;

    @LocalServerPort
    private int porta;

    @Quando("cadastro um usuário com dados válidos")
    public void cadastrarUsuario() {
        String email = "bdd-" + UUID.randomUUID() + "@example.com";
        String senha = "SenhaTeste123!";

        contexto.setEmail(email);
        contexto.setSenha(senha);

        Map<String, Object> dados = Map.of(
            "nome", "Usuario Teste BDD",
            "email", email,
            "senha", senha
        );

        Response resposta = given()
            .port(porta)
            .contentType(ContentType.JSON)
            .body(dados)
        .when()
            .post("/usuarios");

        contexto.setResposta(resposta);

        if (resposta.statusCode() == 201) {
            Long id = resposta.jsonPath().getLong("id");
            if (id != null) {
                contexto.getUsuariosCriados().add(id);
            }
        }
    }

    @Entao("o JSON deve conter os dados do usuário cadastrado")
    public void verificarDados() {
        contexto.getResposta().then()
            .body("id", notNullValue())
            .body("nome", equalTo("Usuario Teste BDD"))
            .body("email", equalTo(contexto.getEmail()))
            .body("role", equalTo("USER"));
    }

    @Entao("o JSON não deve expor a senha")
    public void verificarSenha() {
        Map<String, Object> json =
            contexto.getResposta().jsonPath().getMap("$");

        assertFalse(
            json.containsKey("senha"),
            "A API não deve retornar a senha do usuário."
        );
    }

    @Quando("tento cadastrar um usuário com o campo {string} igual a {string}")
    public void cadastrarComCampoInvalido(String campo, String valor) {
        String email = "bdd-" + UUID.randomUUID() + "@example.com";

        Map<String, Object> dados = new java.util.HashMap<>(
            Map.of(
                "nome", "Usuario Teste BDD",
                "email", email,
                "senha", "SenhaTeste123!"
            )
        );

        if (!dados.containsKey(campo)) {
            throw new IllegalArgumentException(
                "Campo de teste desconhecido: " + campo
            );
        }

        dados.put(campo, valor);

        Response resposta = given()
            .port(porta)
            .contentType(ContentType.JSON)
            .body(dados)
        .when()
            .post("/usuarios");

        contexto.setResposta(resposta);
        registrarCasoTenhaCriado(resposta);
    }

    @Dado("que já existe um usuário cadastrado")
    public void usuarioExistente() {
        cadastrarUsuario();
        contexto.getResposta().then().statusCode(201);
    }

    @Quando("tento cadastrar outro usuário com o mesmo e-mail")
    public void cadastrarEmailDuplicado() {
        Map<String, Object> dados = Map.of(
            "nome", "Outro Usuario",
            "email", contexto.getEmail(),
            "senha", "SenhaTeste123!"
        );

        Response resposta = given()
            .port(porta)
            .contentType(ContentType.JSON)
            .body(dados)
        .when()
            .post("/usuarios");

        contexto.setResposta(resposta);
        registrarCasoTenhaCriado(resposta);
    }

    private void registrarCasoTenhaCriado(Response resposta) {
        if (resposta.statusCode() == 201) {
            Long id = resposta.jsonPath().getLong("id");
            if (id != null) {
                contexto.getUsuariosCriados().add(id);
            }
        }
    }

}
