package br.com.folha.folha_pagamento_batch.service;

import br.com.folha.folha_pagamento_batch.auth.TokenService;
import br.com.folha.folha_pagamento_batch.dto.AuthResponseDTO;
import br.com.folha.folha_pagamento_batch.dto.LoginRequestDTO;
import br.com.folha.folha_pagamento_batch.dto.RegisterRequestDTO;
import br.com.folha.folha_pagamento_batch.entity.Usuarios;
import br.com.folha.folha_pagamento_batch.repository.UsuariosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UsuariosRepository usuariosRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;
  private final AuthenticationManager authenticationManager;

  public AuthResponseDTO register(RegisterRequestDTO request) {
    if (usuariosRepository.findByUsername(request.username()).isPresent()) {
      throw new IllegalArgumentException("Username já existe.");
    }
    Usuarios newUser = new Usuarios();
    newUser.setUsername(request.username());
    newUser.setPasswordHash(passwordEncoder.encode(request.password()));
    newUser.setEnabled(true);

    this.usuariosRepository.save(newUser);

    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
        newUser.getUsername(), newUser.getPasswordHash(), newUser.getAuthorities());
    String token = tokenService.generateToken(userDetails);
    return new AuthResponseDTO(newUser.getUsername(), token);
  }

  public AuthResponseDTO login(LoginRequestDTO request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.username(),
            request.password()));

    Usuarios user = usuariosRepository.findByUsername(request.username())
        .orElseThrow(() -> new IllegalArgumentException("Usuário ou senha inválidos."));

    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
        user.getUsername(), user.getPasswordHash(), user.getAuthorities());

    String token = tokenService.generateToken(userDetails);
    return new AuthResponseDTO(user.getUsername(), token);
  }
}
