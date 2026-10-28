package br.com.fiap.microservico_esg.bdd.hooks;

import br.com.fiap.microservico_esg.bdd.support.CenarioContext;
import br.com.fiap.microservico_esg.repository.UsuarioRepository;
import br.com.fiap.microservico_esg.repository.PontoColetaRepository;
import br.com.fiap.microservico_esg.repository.DescarteRepository;

import io.cucumber.java.After;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class LimpezaHooks {

    @Autowired
    private CenarioContext contexto;

    @Autowired
    private UsuarioRepository usuarios;

    @Autowired
    private PontoColetaRepository pontos;

    @Autowired
    private DescarteRepository descartes;

    @After
    public void limparDados() {
        excluirDescartes(contexto.getDescartesCriados());
        excluirPontos(contexto.getPontosColetaCriados());
        excluirUsuarios(contexto.getUsuariosCriados());
    }

    private void excluirDescartes(List<Long> ids) {
        for (int i = ids.size() - 1; i >= 0; i--) {
            Long id = ids.get(i);
            if (descartes.existsById(id)) {
                descartes.deleteById(id);
            }
        }
    }

    private void excluirPontos(List<Long> ids) {
        for (int i = ids.size() - 1; i >= 0; i--) {
            Long id = ids.get(i);
            if (pontos.existsById(id)) {
                pontos.deleteById(id);
            }
        }
    }

    private void excluirUsuarios(List<Long> ids) {
        for (int i = ids.size() - 1; i >= 0; i--) {
            Long id = ids.get(i);
            if (usuarios.existsById(id)) {
                usuarios.deleteById(id);
            }
        }
    }
}
