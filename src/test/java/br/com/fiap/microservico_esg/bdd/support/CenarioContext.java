package br.com.fiap.microservico_esg.bdd.support;

import io.cucumber.spring.ScenarioScope;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ScenarioScope
public class CenarioContext {

    private Response resposta;
    private String email;
    private String senha;
    private String token;

    private final List<Long> usuariosCriados = new ArrayList<>();
    private final List<Long> pontosColetaCriados = new ArrayList<>();
    private final List<Long> descartesCriados = new ArrayList<>();

    public Response getResposta() {
        return resposta;
    }

    public void setResposta(Response resposta) {
        this.resposta = resposta;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Long> getUsuariosCriados() {
        return usuariosCriados;
    }

    public List<Long> getPontosColetaCriados() {
        return pontosColetaCriados;
    }

    public List<Long> getDescartesCriados() {
        return descartesCriados;
    }
}
