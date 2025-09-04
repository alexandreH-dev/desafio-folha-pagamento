package br.com.folha.folha_pagamento_batch.auth;

import br.com.folha.folha_pagamento_batch.entity.Usuarios;
import br.com.folha.folha_pagamento_batch.repository.UsuariosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por carregar os detalhes de um usuário a partir do banco
 * de dados.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UsuariosRepository usuariosRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Usuarios usuario = usuariosRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

    return new UserDetailsImpl(usuario);
  }
}
