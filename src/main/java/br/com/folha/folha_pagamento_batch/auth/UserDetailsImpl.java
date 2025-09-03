package br.com.folha.folha_pagamento_batch.auth;

import br.com.folha.folha_pagamento_batch.entity.Usuarios;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

  private final Usuarios usuario;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // Futuramente, você pode mapear as Roles do usuário aqui.
    return Collections.emptyList();
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
