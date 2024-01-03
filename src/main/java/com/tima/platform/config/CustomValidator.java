package com.tima.platform.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.server.ServerWebInputException;

import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/29/23
 */
@Configuration
@RequiredArgsConstructor
public class CustomValidator {
    private final Validator validator;

    public <T> void validateEntries(T objectDto) {
        Errors errors = new BeanPropertyBindingResult(objectDto, objectDto.getClass().getName());
        validator.validate(objectDto, errors);
        if(errors.hasErrors()) throw new ServerWebInputException(
                Objects.requireNonNull(errors
                        .getFieldErrors()
                        .get(0).getDefaultMessage())
        );
    }
}
