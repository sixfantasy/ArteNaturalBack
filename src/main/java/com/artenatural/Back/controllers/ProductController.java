package com.artenatural.Back.controllers;

import com.artenatural.Back.entities.ArtistData;
import com.artenatural.Back.entities.Product;
import com.artenatural.Back.entities.ProductOptions;
import com.artenatural.Back.entities.User;
import com.artenatural.Back.repositories.ProductOptionsRepository;
import com.artenatural.Back.repositories.ProductRepository;
import com.artenatural.Back.repositories.UserRepository;
import com.artenatural.Back.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionsRepository productOptionsRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/public")
    @Transactional(readOnly = true)
    public List<Product> getAllPublicProducts() {
        return productRepository.findAll();
    }


    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public Product getProductById(@PathVariable int id) {
        return productRepository.findById(id).orElse(null);
    }


    @PostMapping
    public Product createProduct(@RequestHeader("Authorization") String token, @RequestBody Product product) {
        String userID = jwtTokenUtil.getUserIdFromToken(token.substring(7));
        User user = userRepository.findById(Integer.parseInt(userID)).orElse(null);

        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        if (user.getArtistData() == null) {
            ArtistData artistData = new ArtistData();
            artistData.setProducts(new ArrayList<>());
            user.setArtistData(artistData);
        }

        product.setArtist(user.getArtistData());
        Product savedProduct = productRepository.save(product);

        // Guardar opciones si existen
        if (product.getOptions() != null) {
            for (ProductOptions option : product.getOptions()) {
                option.setProduct(savedProduct);
                productOptionsRepository.save(option);
            }
        }

        return savedProduct;
    }


    @PutMapping
    public Product updateProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }


    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable int id) {
        productRepository.deleteById(id);
    }
}
