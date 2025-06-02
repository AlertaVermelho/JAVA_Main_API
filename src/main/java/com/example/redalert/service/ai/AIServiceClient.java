package com.example.redalert.service.ai;

import com.example.redalert.dto.ai.AIClassificationRequestDTO;
import com.example.redalert.dto.ai.AIClassificationResponseDTO;
import com.example.redalert.exception.ai.AIClassificationException;
import com.example.redalert.exception.ai.AIServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class AIServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(AIServiceClient.class);

    private final RestTemplate restTemplate;
    private final String pythonApiBaseUrl;
    private final String classifyTextEndpointPath = "/ia/classify_text";

    public AIServiceClient(RestTemplate restTemplate,
                           @Value("${app.python.ai.baseurl}") String pythonApiBaseUrl) {
        this.restTemplate = restTemplate;
        this.pythonApiBaseUrl = pythonApiBaseUrl;
    }

    public AIClassificationResponseDTO classifyText(AIClassificationRequestDTO requestDto) {
        String urlCompleta = pythonApiBaseUrl + classifyTextEndpointPath;
        logger.debug("Chamando API de IA para classificação: URL={}, Payload={}", urlCompleta, requestDto);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AIClassificationRequestDTO> entity = new HttpEntity<>(requestDto, headers);

        try {
            ResponseEntity<AIClassificationResponseDTO> responseEntity = restTemplate.exchange(
                    urlCompleta,
                    HttpMethod.POST,
                    entity,
                    AIClassificationResponseDTO.class
            );

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                AIClassificationResponseDTO responseBody = responseEntity.getBody();
                if (responseBody != null) {
                    logger.info("Classificação recebida da API de IA para alertId {}: Tipo={}, Severidade={}",
                            responseBody.getAlertId(),
                            responseBody.getClassifiedType(),
                            responseBody.getClassifiedSeverity());
                    return responseBody;
                } else {
                    logger.error("Resposta 2xx da API de IA, mas o corpo da resposta é nulo. URL: {}", urlCompleta);
                    throw new AIClassificationException("Resposta bem-sucedida da IA, mas corpo da resposta vazio.");
                }
            } else {
                String responseBodyErrorString = "Corpo da resposta de erro não disponível ou nulo.";
                if (responseEntity.hasBody()) {
                    Object errorBody = responseEntity.getBody();
                    if (errorBody != null) {
                        responseBodyErrorString = errorBody.toString();
                    }
                }
                logger.error("Resposta não sucedida da API de IA: Status={}, Body=[{}]",
                        responseEntity.getStatusCode(), responseBodyErrorString);
                throw new AIClassificationException("Falha na classificação pela API de IA: Status " + responseEntity.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Erro HTTP ao chamar API de IA para classificação: Status={}, Body={}",
                         e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new AIServiceException("Erro de comunicação com o serviço de IA (HTTP: " + e.getStatusCode() + "). Detalhes: " + e.getResponseBodyAsString(), e);
        } catch (ResourceAccessException e) {
            logger.error("Erro de acesso ao recurso (ex: timeout, conexão) ao chamar API de IA: {}", e.getMessage(), e);
            throw new AIServiceException("Não foi possível conectar ao serviço de IA. Verifique a disponibilidade e a rede.", e);
        } catch (RestClientException e) {
            logger.error("Erro de RestClient ao chamar API de IA para classificação: {}", e.getMessage(), e);
            throw new AIServiceException("Erro durante a comunicação com o serviço de IA.", e);
        } catch (Exception e) {
            logger.error("Erro inesperado ao chamar API de IA para classificação: {}", e.getMessage(), e);
            throw new AIServiceException("Ocorreu um erro inesperado ao processar com o serviço de IA.", e);
        }
    }
}
