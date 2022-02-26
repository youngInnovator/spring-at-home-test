package com.rize.test.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.stream.Stream;

public enum Category {
    ACTOR, //("actor"),
    PAINTER, //("painter"),
    SCULPTOR, //("sculptor");

//    private String value;
//
//    Category(String value) {
//        this.value = value;
//    }
//
//    @JsonCreator
//    public static Category decode(final String value) {
//        return Stream.of(Category.values()).filter(targetEnum -> targetEnum.value.equals(value)).findFirst()
//                .orElseThrow(() ->
//                        new IllegalArgumentException(
//                                "Unknown category " + value + ", Allowed values are " + Arrays.toString(values()))
//                );
//    }
//
//    @JsonValue
//    public String getValue() {
//        return value;
//    }
}
