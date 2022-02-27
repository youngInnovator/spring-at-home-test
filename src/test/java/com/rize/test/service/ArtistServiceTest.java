package com.rize.test.service;

import com.rize.test.model.Artist;
import com.rize.test.model.Category;
import com.rize.test.respository.ArtistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ArtistServiceTest {
    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistService artistService;

    private static final Date BIRTHDAY_DATE = Date.from(Instant.now());

    @BeforeEach
    public void setup(){
        Artist artist = Artist.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .category(Category.ACTOR)
                .email("test@test.com")
                .birthday(BIRTHDAY_DATE)
                .build();
        when(artistRepository.findById(any(Integer.class))).thenReturn(Optional.of(artist));
        when(artistRepository.findAll()).thenReturn(Collections.singletonList(artist));
        when(artistRepository.findAll(any(Specification.class))).thenReturn(Collections.singletonList(artist));

    }

    @Test
    void findArtistById() {
        // When
        Artist artist = artistService.findArtistById(1);

        //Then
        assertArtist(artist);
    }

    @ParameterizedTest
    @CsvSource({",,","ACTOR,,",",Fir,",",,10","ACTOR,Fir,","ACTOR,,10",",Fir,10","ACTOR,Fir,10"})
    void findArtistsForValidValues(String category, String search, Integer birthdayMonth) {
        // When
        List<Artist> artists = artistService.findArtists(category, search, birthdayMonth);

        //Then
        assertArtist(artists.get(0));
    }

    @Test
    void findArtistsForInValidValues() {
        // When
        Exception exception = assertThrows(ConstraintViolationException.class, () -> artistService.findArtists("ATOR", null, null));

        //Then
        assertTrue(exception.getMessage().contains("Category value ATOR is incorrect. It must be in"));
    }

    private void assertArtist(Artist artist){
        assertEquals("FirstName", artist.getFirstName());
        assertEquals("LastName", artist.getLastName());
        assertEquals(Category.ACTOR, artist.getCategory());
        assertEquals("test@test.com", artist.getEmail());
        assertEquals(BIRTHDAY_DATE, artist.getBirthday());
    }
}