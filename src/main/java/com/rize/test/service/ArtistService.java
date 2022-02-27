package com.rize.test.service;

import com.rize.test.model.Artist;
import com.rize.test.respository.ArtistRepository;
import com.rize.test.specification.ArtistSpecificationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.util.StringUtils.hasLength;

@Service
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    public List<Artist> findArtists(String category, String search, Integer birthdayMonth) {
        if(isFilter(category, search, birthdayMonth)){
            Specification<Artist> artistSpecification = new ArtistSpecificationBuilder()
                    .withCategory(category)
                    .withSearch(search)
                    .withBirthdayMonth(birthdayMonth)
                    .build();

            return artistRepository.findAll(artistSpecification);
        } else {
            return artistRepository.findAll();
        }
    }

    private boolean isFilter(String category, String search, Integer birthdayMonth) {
        return hasLength(category) || hasLength(search) || birthdayMonth != null;
    }
}
