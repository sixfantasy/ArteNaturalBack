package com.artenatural.Back.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Getter
@Setter
public class PurchaseItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    private Product product;
    private int quantity;
    private double price;
    private double total;
    private boolean detailsRequired;
    private String productDetails;
    @JsonIgnore
    @ManyToOne
    private Purchase purchase;
}
