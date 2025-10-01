package com.artenatural.Back.controllers;

import com.artenatural.Back.entities.Artist;
import com.artenatural.Back.repositories.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public class ArtistController {
    @Autowired
    private ArtistRepository artistRepository;

    @GetMapping
    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

}
