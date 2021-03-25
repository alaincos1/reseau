package fr.ul.miage.reseau.dto;

import fr.ul.miage.reseau.exception.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JsonErrorResponseDto {
    private String message;
    HttpStatus httpStatus;
}
