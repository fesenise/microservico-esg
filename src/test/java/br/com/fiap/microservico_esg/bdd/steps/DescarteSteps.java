package br.com.fiap.microservico_esg.bdd.steps;

import br.com.fiap.microservico_esg.bdd.support.ApiTeste;
import br.com.fiap.microservico_esg.bdd.support.CenarioContext;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import io.restassured.http.Method;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DescarteSteps {
    @Autowired private ApiTeste api;
    @Autowired private CenarioContext contexto;

    @Dado("que existem usuário e ponto de coleta para descarte")
    public void referencias() { api.autenticar(); api.criarPonto(); }

    @Dado("que existe um descarte cadastrado para os testes")
    public void descarteExiste() { api.criarDescarte(); }

    @Quando("registro um descarte válido")
    public void cadastrar() { api.criarDescarte(); }

    @Quando("consulto a lista de descartes")
    public void listar() { api.chamar(Method.GET, "/descartes", true, null); }

    @Quando("consulto o descarte pelo ID")
    public void consultar() { api.chamar(Method.GET, "/descartes/" + api.descarteId(), true, null); }

    @Quando("consulto um descarte inexistente")
    public void ausente() { api.chamar(Method.GET, "/descartes/999999999", true, null); }

    @Quando("excluo o descarte cadastrado")
    public void excluir() { api.chamar(Method.DELETE, "/descartes/" + api.descarteId(), true, null); }

    @Quando("tento excluir um descarte inexistente")
    public void excluirAusente() { api.chamar(Method.DELETE, "/descartes/999999999", true, null); }

    @Quando("registro um descarte com peso zero")
    public void pesoInvalido() {
        Map<String, Object> dados = new HashMap<>(api.dadosDescarte());
        dados.put("pesoKg", 0);
        api.chamar(Method.POST, "/descartes", true, dados);
    }

    @Quando("registro um descarte com usuário inexistente")
    public void usuarioInvalido() {
        Map<String, Object> dados = new HashMap<>(api.dadosDescarte());
        dados.put("usuario", Map.of("id", 999999999));
        api.chamar(Method.POST, "/descartes", true, dados);
    }

    @Quando("registro um descarte com ponto de coleta inexistente")
    public void pontoInvalido() {
        Map<String, Object> dados = new HashMap<>(api.dadosDescarte());
        dados.put("pontoColeta", Map.of("id", 999999999));
        api.chamar(Method.POST, "/descartes", true, dados);
    }

    @Quando("consulto os descartes sem token")
    public void semToken() { api.chamar(Method.GET, "/descartes", false, null); }

    @Entao("a lista contém o descarte criado")
    public void listaContem() { api.exigirItemNaLista(api.descarteId()); }

    @Entao("a resposta identifica o descarte e seus vínculos")
    public void identifica() {
        assertEquals(api.descarteId().longValue(), contexto.getResposta().jsonPath().getLong("id"));
        assertEquals(api.usuarioId().longValue(), contexto.getResposta().jsonPath().getLong("usuario.id"));
        assertEquals(api.pontoId().longValue(), contexto.getResposta().jsonPath().getLong("pontoColeta.id"));
        assertEquals("PLASTICO", contexto.getResposta().jsonPath().getString("tipoResiduo"));
    }

    @Entao("o descarte excluído não deve mais ser encontrado")
    public void exclusaoConfirmada() {
        assertEquals(404, api.chamar(Method.GET, "/descartes/" + api.descarteId(), true, null).statusCode());
    }
}
