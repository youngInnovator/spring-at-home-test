package com.rize.test.controller;

import com.rize.test.exception.ArtistNotFoundException;
import com.rize.test.model.Artist;
import com.rize.test.model.ArtistDto;
import com.rize.test.respository.ArtistRepository;
import com.rize.test.service.ArtistService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingErrorProcessor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/artists")
@Validated
public class ArtistController {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistService artistService;

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(artistService.fromArtist(findArtist(id)));
    }

    @GetMapping
    public ResponseEntity<List<ArtistDto>> index(@RequestParam(value = "category", required = false) String category,
                                                 @RequestParam(value = "search", required = false) String search,
                                                 @RequestParam(value = "birthday-month", required = false)
                                                     @Min (value = 1, message = "birthday-month must be greater than or equal to 1")
                                                     @Max (value = 12, message = "birthday-month must be less than or equal to 12") Integer birthdayMonth) {
        return ResponseEntity.ok(artistService.findArtists(category, search, birthdayMonth));
    }

    @PostMapping
    public ResponseEntity<ArtistDto> create(@Valid @RequestBody ArtistDto artistDto) {
        Artist artist = artistRepository.save(artistService.toArtist(artistDto));
        return ResponseEntity.created(URI.create("/artist/"+artist.getId())).body(artistDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        Artist artist = findArtist(id);
        artistRepository.delete(artist);
        return ResponseEntity.ok(artistService.fromArtist(artist));
    }

    private Artist findArtist(Integer id){
        Optional<Artist> artist = artistRepository.findById(id);
        if(artist.isEmpty()){
            throw new ArtistNotFoundException(id);
        }
        return artist.get();
    }
}
