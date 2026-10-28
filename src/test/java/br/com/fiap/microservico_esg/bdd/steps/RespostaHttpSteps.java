package br.com.fiap.microservico_esg.bdd.steps;

import br.com.fiap.microservico_esg.bdd.support.CenarioContext;
import io.cucumber.java.pt.Entao;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RespostaHttpSteps {
    @Autowired private CenarioContext contexto;

    @Entao("o corpo da resposta HTTP deve estar vazio")
    public void corpoVazio() {
        assertTrue(contexto.getResposta().asString().isBlank(),
                "Esperado corpo vazio, mas recebido: " + contexto.getResposta().asString());
    }
}
