package com.academia.academia_api.controllers;
import com.academia.academia_api.DTOs.AuthDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity login (@RequestBody @Valid AuthDTO data) {

        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.senha());
        var authentication = this.authenticationManager.authenticate(usernamePassword);

        return ResponseEntity.ok().build();
    }

}
