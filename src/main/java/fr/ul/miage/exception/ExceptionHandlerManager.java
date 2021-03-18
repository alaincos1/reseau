package fr.ul.miage.exception;

import fr.ul.miage.dto.JsonErrorResponseDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionHandlerManager {
    public static JsonErrorResponseDto handleContentTypeNotFoundException(ApiException e) {
        log.error(e.getMessage());
        return new JsonErrorResponseDto(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}