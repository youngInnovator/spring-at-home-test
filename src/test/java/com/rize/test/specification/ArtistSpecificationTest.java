package com.rize.test.specification;

import com.rize.test.model.Artist;
import org.hibernate.query.criteria.internal.predicate.ComparisonPredicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ArtistSpecificationTest {

    @InjectMocks
    private ArtistSpecification artistSpecification;

    @Mock
    private SearchCriteria searchCriteria;

    @Mock
    private Root<Artist> root;

    @Mock
    private CriteriaBuilder builder;

    private String key;
    private Object value;

    @BeforeEach
    public void setup(){
        key = "key";
        value = new Object();
    }

    @ParameterizedTest
    @CsvSource({":,true","month,true","other,false"})
    void getPredicate(String operation, boolean expected) {
        // Given
        when(searchCriteria.getOperation()).thenReturn(operation);
        when(searchCriteria.getKey()).thenReturn(key);
        when(searchCriteria.getValue()).thenReturn(value);
        when(root.get(key)).thenReturn(any());
        when(builder.equal(any(), value)).thenReturn(new ComparisonPredicate(null, null, (Expression<?>) null, null));

        // When
        Predicate predicate = artistSpecification.toPredicate(root, null, builder);


        //Then
        assertThat(predicate != null).isEqualTo(expected);
    }

    @Test
    void getPredicateForLike() {
        // Given
        when(searchCriteria.getOperation()).thenReturn("like");
        when(searchCriteria.getKey()).thenReturn(key);
        when(searchCriteria.getValue()).thenReturn(key);
        when(root.get(key)).thenReturn(any());
        when(builder.like(any(), key)).thenReturn(new ComparisonPredicate(null, null, (Expression<?>) null, null));

        // When
        Predicate predicate = artistSpecification.toPredicate(root, null, builder);


        //Then
        assertNotNull(predicate);
    }

}