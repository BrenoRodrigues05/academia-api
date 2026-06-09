package com.academia.academia_api.repository;

import com.academia.academia_api.entity.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository <Usuarios, String> {

    Usuarios findByLogin (String login);

    boolean existsByLogin(String login);
}
