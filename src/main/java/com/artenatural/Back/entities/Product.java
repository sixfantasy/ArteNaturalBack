package com.artenatural.Back.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String description;
    private double price;
    private String image;
    @JoinColumn(name = "artist_id")
    @ManyToOne
    private Artist artist;
}
