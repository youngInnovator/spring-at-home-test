package com.rize.test.specification;

import com.rize.test.model.Artist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ArtistSpecificationBuilderTest {

    private ArtistSpecificationBuilder artistSpecificationBuilder;

    @BeforeEach
    public void setup() {
        artistSpecificationBuilder = new ArtistSpecificationBuilder();
    }

    @Test
    void testBuildSpecificationWithoutAnyValue() {
        // When
        Specification<Artist> specification = artistSpecificationBuilder.build();

        //Then
        assertNull(specification);
    }

    @ParameterizedTest
    @CsvSource({"ACTOR,,", ",Fir,", ",,10", "ACTOR,Fir,", "ACTOR,,10", ",Fir,10", "ACTOR,Fir,10"})
    void testBuildSpecificationWithValues(String category, String search, Integer birthdayMonth) {
        // When
        Specification<Artist> specification = artistSpecificationBuilder
                .withCategory(category)
                .withSearch(search)
                .withBirthdayMonth(birthdayMonth)
                .build();

        //Then
        assertNotNull(specification);
    }

}