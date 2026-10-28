package br.com.fiap.microservico_esg.bdd.steps;

import br.com.fiap.microservico_esg.bdd.support.ApiTeste;
import br.com.fiap.microservico_esg.bdd.support.CenarioContext;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import io.restassured.http.Method;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AutenticacaoSteps {
    @Autowired private ApiTeste api;
    @Autowired private CenarioContext contexto;

    @Dado("que estou autenticado na API ESG")
    public void estouAutenticado() { api.autenticar(); }

    @Dado("que existe um usuário para autenticação")
    public void usuarioExiste() { api.criarUsuario(); }

    @Quando("faço login com minhas credenciais corretas")
    public void loginCorreto() {
        api.chamar(Method.POST, "/auth/login", false,
                Map.of("email", contexto.getEmail(), "senha", contexto.getSenha()));
    }

    @Quando("faço login com uma senha incorreta")
    public void loginIncorreto() {
        api.chamar(Method.POST, "/auth/login", false,
                Map.of("email", contexto.getEmail(), "senha", "SenhaIncorreta123!"));
    }

    @Quando("consulto usuários sem apresentar um token")
    public void semToken() { api.chamar(Method.GET, "/usuarios", false, null); }

    @Quando("consulto usuários com meu token JWT")
    public void comToken() { api.chamar(Method.GET, "/usuarios", true, null); }

    @Entao("o token JWT retornado deve estar preenchido")
    public void tokenValido() {
        String token = contexto.getResposta().jsonPath().getString("token");
        assertNotNull(token, "Campo token ausente");
        assertTrue(token.split("\\.").length == 3, "Formato JWT esperado: header.payload.signature");
    }
}
