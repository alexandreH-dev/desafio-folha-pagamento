package br.com.folha.folha_pagamento_batch.auth;

import br.com.folha.folha_pagamento_batch.entity.Usuarios;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Implementação da interface UserDetails do Spring Security.
 * Atua como um 'Adaptador', envolvendo a entidade 'Usuarios' do banco de dados e
 * traduzindo suas informações (username, senha, permissões, status) para o formato que o
 * Spring Security utiliza internamente para realizar a autenticação e autorização.
 */
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

  private final Usuarios usuario;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return usuario.getAuthorities();
  }

  @Override
  public String getPassword() {
    return usuario.getPasswordHash();
  }

  @Override
  public String getUsername() {
    return usuario.getUsername();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return usuario.getEnabled();
  }
}
