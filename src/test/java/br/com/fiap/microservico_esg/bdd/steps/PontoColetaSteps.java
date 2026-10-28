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

public class PontoColetaSteps {
    @Autowired private ApiTeste api;
    @Autowired private CenarioContext contexto;

    @Dado("que existe um ponto de coleta cadastrado para os testes")
    public void pontoExiste() { api.criarPonto(); }

    @Quando("cadastro um novo ponto de coleta")
    public void cadastrar() { api.criarPonto(); }

    @Quando("consulto a lista de pontos de coleta")
    public void listar() { api.chamar(Method.GET, "/pontos-coleta", true, null); }

    @Quando("consulto o ponto de coleta pelo ID")
    public void consultar() { api.chamar(Method.GET, "/pontos-coleta/" + api.pontoId(), true, null); }

    @Quando("consulto um ponto de coleta inexistente")
    public void ausente() { api.chamar(Method.GET, "/pontos-coleta/999999999", true, null); }

    @Quando("excluo o ponto de coleta cadastrado")
    public void excluir() { api.chamar(Method.DELETE, "/pontos-coleta/" + api.pontoId(), true, null); }

    @Quando("tento excluir um ponto de coleta inexistente")
    public void excluirAusente() { api.chamar(Method.DELETE, "/pontos-coleta/999999999", true, null); }

    @Quando("cadastro ponto de coleta com estado inválido")
    public void estadoInvalido() {
        Map<String, Object> dados = new HashMap<>(api.dadosPonto());
        dados.put("estado", "Sao Paulo");
        api.chamar(Method.POST, "/pontos-coleta", true, dados);
    }

    @Quando("consulto os pontos de coleta sem token")
    public void semToken() { api.chamar(Method.GET, "/pontos-coleta", false, null); }

    @Entao("a lista contém o ponto de coleta criado")
    public void listaContem() { api.exigirItemNaLista(api.pontoId()); }

    @Entao("a resposta identifica o ponto de coleta criado")
    public void identifica() {
        assertEquals(api.pontoId().longValue(), contexto.getResposta().jsonPath().getLong("id"));
        assertEquals("Ecoponto BDD", contexto.getResposta().jsonPath().getString("nome"));
        assertEquals("SP", contexto.getResposta().jsonPath().getString("estado"));
    }

    @Entao("o ponto excluído não deve mais ser encontrado")
    public void exclusaoConfirmada() {
        assertEquals(404, api.chamar(Method.GET, "/pontos-coleta/" + api.pontoId(), true, null).statusCode());
    }
}
