package com.artenatural.Back.repositories;

import com.artenatural.Back.entities.ArtistData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistDataRepository extends JpaRepository<ArtistData, Integer> {
}
