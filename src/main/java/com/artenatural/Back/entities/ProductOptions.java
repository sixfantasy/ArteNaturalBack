package com.artenatural.Back.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Getter
@Setter
public class ProductOptions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String category;
    private String option;
    private double price;
    @ManyToOne
    private Product product;
}
