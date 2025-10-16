package com.artenatural.Back.controllers;

import com.artenatural.Back.entities.*;
import com.artenatural.Back.repositories.ProductRepository;
import com.artenatural.Back.repositories.PurchaseRepository;
import com.artenatural.Back.repositories.UserRepository;
import com.artenatural.Back.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/purchases")
@CrossOrigin
public class PurchaseController {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // Endpoint protegido: solo usuarios autenticados pueden comprar
    @PostMapping
    public ResponseEntity<Purchase> createPurchase(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PurchaseRequest request) {

        String token = authHeader.substring(7);
        String userId = jwtTokenUtil.getUserIdFromToken(token);
        User user = userRepository.findById(Integer.parseInt(userId)).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        // Crear la compra
        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setStatus(Purchase.Status.ACTIVE);
        purchase.setTotal(request.getTotalPrice());

        // Crear los items
        List<PurchaseItem> items = new ArrayList<>();
        PurchaseItem item = new PurchaseItem();
        item.setPurchase(purchase);
        item.setProduct(productRepository.findById(request.getProductId()).orElse(null));
        item.setQuantity(1);
        item.setPrice(request.getTotalPrice());
        item.setTotal(request.getTotalPrice());

        // Aquí podrías guardar las opciones seleccionadas como JSON o en un campo
        item.setProductDetails(buildDetailsString(request.getSelectedOptions()));

        items.add(item);
        purchase.setItems(items);

        Purchase saved = purchaseRepository.save(purchase);
        return ResponseEntity.ok(saved);
    }

    private String buildDetailsString(Object options) {
        // Por ahora, convierte a JSON o string simple
        return options != null ? options.toString() : "";
    }

    // DTO para la solicitud
    public static class PurchaseRequest {
        private int productId;
        private double totalPrice;
        private Object selectedOptions; // o un Map<String, String>

        // getters y setters
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }
        public double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
        public Object getSelectedOptions() { return selectedOptions; }
        public void setSelectedOptions(Object selectedOptions) { this.selectedOptions = selectedOptions; }
    }
}