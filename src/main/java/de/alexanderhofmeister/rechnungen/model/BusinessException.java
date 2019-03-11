package de.alexanderhofmeister.rechnungen.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BusinessException extends Throwable {

    private final String message;

}
