package com.rize.test.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ErrorDto {
    private List<String> errors;

    public ErrorDto(String ... errors){
        this.errors = Arrays.asList(errors);
    }
}
