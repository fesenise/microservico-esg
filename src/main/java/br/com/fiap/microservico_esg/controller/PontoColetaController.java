package br.com.fiap.microservico_esg.controller;

import br.com.fiap.microservico_esg.model.PontoColeta;
import br.com.fiap.microservico_esg.repository.PontoColetaRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pontos-coleta")
public class PontoColetaController {

    private final PontoColetaRepository repository;

    public PontoColetaController(PontoColetaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<PontoColeta> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PontoColeta> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PontoColeta> cadastrar(@RequestBody @Valid PontoColeta pontoColeta) {
        PontoColeta pontoSalvo = repository.save(pontoColeta);
        return ResponseEntity.status(201).body(pontoSalvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}