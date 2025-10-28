package com.artenatural.Back.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Purchase {

    public enum Status {
        ACTIVE,
        COMPLETED,
        CANCELLED,
        PROCESSING,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Status status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("orders") // Evita bucle: Purchase → User → Purchase
    private User user;

    private String customerName;
    private String customerEmail;
    private String customerAddress;
    private String paymentMethod; // "card", "paypal", "bizum"

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("purchase") // Evita bucle: PurchaseItem → Purchase → PurchaseItem
    private List<PurchaseItem> items = new ArrayList<>();


    private double total;


}