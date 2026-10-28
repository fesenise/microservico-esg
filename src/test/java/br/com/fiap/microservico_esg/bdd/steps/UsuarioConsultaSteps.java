package br.com.fiap.microservico_esg.bdd.steps;

import br.com.fiap.microservico_esg.bdd.support.ApiTeste;
import br.com.fiap.microservico_esg.bdd.support.CenarioContext;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import io.restassured.http.Method;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioConsultaSteps {
    @Autowired private ApiTeste api;
    @Autowired private CenarioContext contexto;

    @Quando("listo os usuários cadastrados")
    public void listar() { api.chamar(Method.GET, "/usuarios", true, null); }

    @Quando("busco meu usuário pelo ID")
    public void consultar() { api.chamar(Method.GET, "/usuarios/" + api.usuarioId(), true, null); }

    @Quando("busco um usuário inexistente")
    public void ausente() { api.chamar(Method.GET, "/usuarios/999999999", true, null); }

    @Quando("excluo meu usuário pelo ID")
    public void excluir() { api.chamar(Method.DELETE, "/usuarios/" + api.usuarioId(), true, null); }

    @Quando("tento excluir um usuário inexistente")
    public void excluirAusente() { api.chamar(Method.DELETE, "/usuarios/999999999", true, null); }

    @Entao("a lista contém meu usuário")
    public void listaContem() { api.exigirItemNaLista(api.usuarioId()); }

    @Entao("a resposta identifica meu usuário")
    public void identifica() {
        assertEquals(api.usuarioId().longValue(), contexto.getResposta().jsonPath().getLong("id"));
        assertEquals(contexto.getEmail(), contexto.getResposta().jsonPath().getString("email"));
        assertNull(contexto.getResposta().jsonPath().get("senha"), "Senha não deve ser exposta");
    }

    @Entao("o usuário excluído não deve mais ser encontrado")
    public void exclusaoConfirmada() {
        assertEquals(404, api.chamar(Method.GET, "/usuarios/" + api.usuarioId(), true, null).statusCode());
    }
}
