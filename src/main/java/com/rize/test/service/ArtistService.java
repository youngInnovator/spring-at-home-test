package com.rize.test.service;

import com.rize.test.model.Artist;
import com.rize.test.model.ArtistDto;
import com.rize.test.respository.ArtistRepository;
import com.rize.test.specification.ArtistSpecification;
import com.rize.test.specification.ArtistSpecificationBuilder;
import com.rize.test.specification.SearchCriteria;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.Assert.notNull;
import static org.springframework.util.StringUtils.hasLength;

@Service
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;


    public Artist toArtist(ArtistDto artistDto) {
        Artist artist = new Artist();
        BeanUtils.copyProperties(artistDto, artist);
        return artist;
    }

    public ArtistDto fromArtist(Artist artist) {
        ArtistDto artistDto = new ArtistDto();
        BeanUtils.copyProperties(artist, artistDto);
        artistDto.setCategory(artist.getCategory());
        return artistDto;
    }

    public List<ArtistDto> fromArtist(List<Artist> artistList) {
        return artistList
                .stream()
                .map(this::fromArtist)
                .collect(Collectors.toList());
    }

    public List<ArtistDto> findArtists(String category, String search, Integer birthdayMonth) {
        if(isFilter(category, search, birthdayMonth)){
            Specification<Artist> artistSpecification = new ArtistSpecificationBuilder()
                    .withCategory(category)
                    .withSearch(search)
                    .withBirthdayMonth(birthdayMonth)
                    .build();

            return fromArtist(artistRepository.findAll(artistSpecification));
        } else {
            return fromArtist(artistRepository.findAll());
        }
    }

    private boolean isFilter(String category, String search, Integer birthdayMonth) {
        return hasLength(category) || hasLength(search) || birthdayMonth != null;
    }
}
