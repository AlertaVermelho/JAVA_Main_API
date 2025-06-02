package com.example.redalert.exception.handler;

import com.example.redalert.dto.users.ErrorResponseDTO;
import com.example.redalert.exception.ai.AIClassificationException;
import com.example.redalert.exception.ai.AIServiceException;
import com.example.redalert.exception.users.EmailJaCadastradoException;
import com.example.redalert.exception.users.SenhaIncorretaException;
import com.example.redalert.exception.users.UsuarioNaoEncontradoException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger globalLogger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> handleUsuarioNaoEncontradoException(UsuarioNaoEncontradoException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                ((ServletWebRequest)request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailJaCadastradoException(EmailJaCadastradoException ex, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                ((ServletWebRequest)request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(SenhaIncorretaException.class)
    public ResponseEntity<ErrorResponseDTO> handleSenhaIncorretaException(SenhaIncorretaException ex, WebRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                ((ServletWebRequest)request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                "Credenciais inválidas ou falha na autenticação.",
                ((ServletWebRequest)request).getRequest().getRequestURI()
        );
        globalLogger.warn("Falha na autenticação: {} para o request {}", ex.getMessage(), ((ServletWebRequest)request).getRequest().getRequestURI());
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(AIClassificationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAIClassificationException(AIClassificationException ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                "Erro durante a classificação pelo serviço de IA: " + ex.getMessage(),
                ((ServletWebRequest)request).getRequest().getRequestURI()
        );
        globalLogger.error("AIClassificationException: {} para o request {}", ex.getMessage(), ((ServletWebRequest)request).getRequest().getRequestURI(), ex);
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(AIServiceException.class)
    public ResponseEntity<ErrorResponseDTO> handleAIServiceException(AIServiceException ex, WebRequest request) {
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                "Serviço de IA indisponível ou erro de comunicação: " + ex.getMessage(),
                ((ServletWebRequest)request).getRequest().getRequestURI()
        );
        globalLogger.error("AIServiceException: {} para o request {}", ex.getMessage(), ((ServletWebRequest)request).getRequest().getRequestURI(), ex);
        return new ResponseEntity<>(errorResponse, status);
    }
    
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex, 
            @NonNull HttpHeaders headers, 
            @NonNull HttpStatusCode httpStatus,
            @NonNull WebRequest request) {
        
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<Map<String, String>> errorDetails = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> Map.of(
                        "field", fieldError.getField(),
                        "message", fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Erro de validação não especificado"
                ))
                .collect(Collectors.toList());

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                "Erro de validação nos dados de entrada.",
                ((ServletWebRequest)request).getRequest().getRequestURI(),
                errorDetails
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex, WebRequest request) {
        globalLogger.error("Erro genérico não tratado capturado: {}", ex.getMessage(), ex);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                "Ocorreu um erro inesperado no servidor. Por favor, tente novamente mais tarde.",
                ((ServletWebRequest)request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}
