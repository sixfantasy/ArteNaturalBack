package com.artenatural.Back.controllers;

import com.artenatural.Back.entities.ArtistData;
import com.artenatural.Back.entities.Product;
import com.artenatural.Back.entities.ProductOptions;
import com.artenatural.Back.entities.User;
import com.artenatural.Back.repositories.ProductOptionsRepository;
import com.artenatural.Back.repositories.ProductRepository;
import com.artenatural.Back.repositories.UserRepository;
import com.artenatural.Back.utils.JwtTokenUtil;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable int id) {
        return productRepository.findById(id).orElse(null);
    }
    @PostMapping()
    public Product createProduct(@RequestHeader("Authorization") String token, @RequestBody Product product) {
        String userID = jwtTokenUtil.getUserIdFromToken(token.substring(7));


        User user = userRepository.findById(Integer.parseInt(userID)).get();
        product.setArtist(user.getArtistData());
        //product.getOptions().set
        if (user.getArtistData() == null) {
            ArtistData artistData = new ArtistData();
            artistData.setProducts(new ArrayList<>());
            artistData.getProducts().add(product);
            user.setArtistData(artistData);
        }
        else{


        productRepository.save(product);
            ProductOptions po = product.getOptions().get(0);

            po.setProduct(product);
            productOptionsRepository.save(po);

        }


        return product;
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
