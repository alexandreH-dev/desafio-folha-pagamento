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

    // Cria UserDetails para o novo usuário para gerar o token
    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
        newUser.getUsername(), newUser.getPasswordHash(), savedUser.getAuthorities());
    String token = tokenService.generateToken(userDetails);
    return new AuthResponseDTO(token);
  }

  public AuthResponseDTO login(LoginRequestDTO request) {
    // Valida as credenciais usando o Spring Security
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.username(),
            request.password()));
    // Se a autenticação foi bem-sucedida, busca o usuário e gera o token
    var user = usuariosRepository.findByUsername(request.username())
        .orElseThrow(() -> new IllegalArgumentException("Usuário ou senha inválidos."));

    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
        user.getUsername(), user.getPasswordHash(), user.getAuthorities());

    String token = tokenService.generateToken(userDetails);
    return new AuthResponseDTO(token);
  }
}
