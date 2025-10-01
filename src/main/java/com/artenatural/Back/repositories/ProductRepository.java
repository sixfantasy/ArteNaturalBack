package com.artenatural.Back.repositories;

import com.artenatural.Back.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
