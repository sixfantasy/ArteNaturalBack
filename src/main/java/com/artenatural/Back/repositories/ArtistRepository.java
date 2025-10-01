package com.artenatural.Back.repositories;

import com.artenatural.Back.entities.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Integer> {
}
