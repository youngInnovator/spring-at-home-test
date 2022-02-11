package com.rize.test.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/artists")
public class ArtistController {

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable String id) {
        return ResponseEntity.ok(null);
    }

    @GetMapping
    public ResponseEntity<?> index() {
        return ResponseEntity.ok(null);
    }

    @PostMapping
    public ResponseEntity<?> create() {
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id){
        return ResponseEntity.noContent().build();
    }
}
