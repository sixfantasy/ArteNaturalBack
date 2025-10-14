package com.artenatural.Back.controllers;

import com.artenatural.Back.entities.Purchase;
import com.artenatural.Back.entities.User;
import com.artenatural.Back.repositories.PurchaseRepository;
import com.artenatural.Back.repositories.UserRepository;
import com.artenatural.Back.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping
    public List<Purchase> getAllPurchasesFromUser(@RequestHeader("Authorization") String token) {
        String userID = jwtTokenUtil.getUserIdFromToken(token.substring(7));
        return purchaseRepository.findByUser_Id(Integer.parseInt(userID));
    }
    public Purchase createPurchase(@RequestHeader("Authorization") String token, @RequestBody Purchase purchase) {
        String userID = jwtTokenUtil.getUserIdFromToken(token.substring(7));
        User user = userRepository.findById(Integer.parseInt(userID)).get();
        purchase.setStatus(Purchase.Status.ACTIVE);
        purchase.setUser(user);
        return purchaseRepository.save(purchase);
    }
    public Purchase getActivePurchase(@RequestHeader("Authorization") String token) {
        String userID = jwtTokenUtil.getUserIdFromToken(token.substring(7));
        User user = userRepository.findById(Integer.parseInt(userID)).get();
        return purchaseRepository.findByUser_IdAndStatus(user, Purchase.Status.ACTIVE);
    }
}
