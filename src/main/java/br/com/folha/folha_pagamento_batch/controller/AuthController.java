package br.com.folha.folha_pagamento_batch.controller;

import br.com.folha.folha_pagamento_batch.dto.AuthResponseDTO;
import br.com.folha.folha_pagamento_batch.dto.LoginRequestDTO;
import br.com.folha.folha_pagamento_batch.dto.RegisterRequestDTO;
import br.com.folha.folha_pagamento_batch.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {
    return ResponseEntity.ok(authService.register(request));
  }
}
