package com.artenatural.Back.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

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
    @JsonIgnore
    @ManyToOne
    private User user;
    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    List<PurchaseItem> items;
    private double total;


}
