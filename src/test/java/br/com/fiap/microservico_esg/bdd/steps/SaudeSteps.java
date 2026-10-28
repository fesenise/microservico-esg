package br.com.fiap.microservico_esg.bdd.steps;

import br.com.fiap.microservico_esg.bdd.support.ApiTeste;
import br.com.fiap.microservico_esg.bdd.support.CenarioContext;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import io.restassured.http.Method;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SaudeSteps {
    @Autowired private ApiTeste api;
    @Autowired private CenarioContext contexto;

    @Quando("consulto a saúde do microsserviço sem autenticação")
    public void consultarHealth() {
        api.chamar(Method.GET, "/actuator/health", false, null);
    }

    @Entao("o estado do microsserviço deve estar UP")
    public void estadoUp() {
        assertEquals("UP", contexto.getResposta().jsonPath().getString("status"));
    }
}
