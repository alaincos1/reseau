package fr.ul.miage.dto;

import fr.ul.miage.exception.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JsonErrorResponseDto {
    private String message;
    HttpStatus httpStatus;
}
