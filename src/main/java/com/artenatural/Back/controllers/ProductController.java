package com.artenatural.Back.controllers;

import com.artenatural.Back.entities.Product;
import com.artenatural.Back.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {
    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable int id) {
        return productRepository.findById(id).orElse(null);
    }
    @PostMapping()
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @PutMapping()
    public Product updateProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable int id) {
        productRepository.deleteById(id);
    }
}
