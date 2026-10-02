package br.com.fiap.microservico_esg.repository;

import br.com.fiap.microservico_esg.model.Descarte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DescarteRepository extends JpaRepository<Descarte, Long> {
}