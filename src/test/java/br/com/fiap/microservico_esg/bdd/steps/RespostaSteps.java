package br.com.fiap.microservico_esg.bdd.steps;

import br.com.fiap.microservico_esg.bdd.support.CenarioContext;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.containsString;

public class RespostaSteps {

    @Autowired
    private CenarioContext contexto;

    @LocalServerPort
    private int porta;

    @Dado("que o microsserviço ESG está disponível")
    public void verificarDisponibilidade() {
        given()
            .port(porta)
        .when()
            .get("/actuator/health")
        .then()
            .statusCode(200)
            .body("status", equalTo("UP"));
    }

    @Entao("a resposta deve ter status {int}")
    public void verificarStatus(int status) {
        contexto.getResposta()
            .then()
            .statusCode(status);
    }

    @Entao("o JSON deve respeitar o contrato {string}")
    public void verificarContratoJson(String arquivo) {
        contexto.getResposta()
            .then()
            .body(matchesJsonSchemaInClasspath("schemas/" + arquivo));
    }


    @Entao("a mensagem de erro deve mencionar {string}")
    public void verificarMensagemDeErro(String texto) {
        contexto.getResposta()
            .then()
            .body("mensagem", containsString(texto));
    }

}
