package br.com.fiap.microservico_esg.repository;

import br.com.fiap.microservico_esg.model.PontoColeta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PontoColetaRepository extends JpaRepository<PontoColeta, Long> {
}