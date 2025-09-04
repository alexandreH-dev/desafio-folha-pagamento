package br.com.folha.folha_pagamento_batch.auth;

import br.com.folha.folha_pagamento_batch.dto.ErrorResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * É acionado pelo Spring Security quando um utilizador não autenticado tenta
 * aceder
 * a um recurso protegido. Isto inclui casos onde o token JWT está em falta,
 * é inválido ou, como neste caso, está expirado.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException, ServletException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType("application/json; charset=UTF-8");

    ErrorResponseDTO errorResponse = new ErrorResponseDTO(
        HttpStatus.UNAUTHORIZED.value(),
        "Não autorizado. O token de autenticação é inválido, está expirado ou não foi fornecido.");

    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}
