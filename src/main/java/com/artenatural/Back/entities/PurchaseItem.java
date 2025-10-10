package com.artenatural.Back.entities;

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
    @ManyToOne
    private Purchase purchase;
}
