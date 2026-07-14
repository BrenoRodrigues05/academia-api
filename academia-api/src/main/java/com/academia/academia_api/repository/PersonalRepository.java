package com.academia.academia_api.repository;

import com.academia.academia_api.entity.Personal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalRepository extends JpaRepository<Personal, Long> {
    Optional<Personal> findByEmailContainingIgnoreCase(String email);
    Optional<Personal> findByCref(String cref);
    List<Personal> findByNomeContainingIgnoreCase(String nome);
    List<Personal> findByAtivoFalse();
    List<Personal> findByAtivoTrue();
    Optional<Personal> findByUsuarioId(Long usuarioId);
}
