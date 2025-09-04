package br.com.folha.folha_pagamento_batch.exception;

import br.com.folha.folha_pagamento_batch.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Classe central para tratamento de exceções em toda a aplicação.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Captura exceções de negócio, como "usuário já existe".
   *
   * @param ex A exceção lançada.
   * @return Uma resposta com status 409 (Conflict) e a mensagem da exceção.
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
    ErrorResponseDTO errorResponse = new ErrorResponseDTO(HttpStatus.CONFLICT.value(), ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
  }

  /**
   * Captura exceções de credenciais inválidas durante o login.
   *
   * @param ex A exceção lançada pelo Spring Security.
   * @return Uma resposta com status 401 (Unauthorized) e uma mensagem amigável.
   */
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponseDTO> handleBadCredentialsException(BadCredentialsException ex) {
    ErrorResponseDTO errorResponse = new ErrorResponseDTO(HttpStatus.UNAUTHORIZED.value(),
        "Usuário ou senha inválidos.");
    return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
  }

  /**
   * Captura qualquer outra exceção não tratada para evitar expor detalhes
   * internos.
   *
   * @param ex A exceção genérica.
   * @return Uma resposta com status 500 (Internal Server Error) e uma mensagem
   *         genérica.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
    ErrorResponseDTO errorResponse = new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Ocorreu um erro inesperado no servidor.");

    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(ResourceNotFoundException ex) {
    // Cria a resposta JSON padronizada
    ErrorResponseDTO errorResponse = new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    // Retorna a resposta com o status 404
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }
}
