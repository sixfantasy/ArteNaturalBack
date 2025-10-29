package com.artenatural.Back.controllers;

import com.artenatural.Back.entities.Purchase;
import com.artenatural.Back.entities.PurchaseItem;
import com.artenatural.Back.entities.User;
import com.artenatural.Back.repositories.ProductRepository;
import com.artenatural.Back.repositories.PurchaseRepository;
import com.artenatural.Back.repositories.UserRepository;
import com.artenatural.Back.utils.JwtTokenUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @PostMapping
    public ResponseEntity<Purchase> createPurchase(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PurchaseRequest request) {

        try {
            String token = authHeader.substring(7);
            String userId = jwtTokenUtil.getUserIdFromToken(token);
            User user = userRepository.findById(Integer.parseInt(userId))
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Crear la compra
            Purchase purchase = new Purchase();
            purchase.setUser(user);
            purchase.setStatus(Purchase.Status.COMPLETED);
            purchase.setTotal(request.getTotal());

            // ✅ Guardar datos del cliente y método de pago
            if (request.getCustomer() != null) {
                purchase.setCustomerName(request.getCustomer().getName());
                purchase.setCustomerEmail(request.getCustomer().getEmail());
                purchase.setCustomerAddress(request.getCustomer().getAddress());
            }
            purchase.setPaymentMethod(request.getPaymentMethod()); // ✅


            // Crear los items
            List<PurchaseItem> items = new ArrayList<>();
            for (CartItemDto itemDto : request.getItems()) {
                PurchaseItem item = new PurchaseItem();
                item.setPurchase(purchase);
                item.setProduct(productRepository.findById(itemDto.getProductId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado")));
                item.setQuantity(1);
                item.setPrice(itemDto.getBasePrice());
                item.setTotal(itemDto.getTotalPrice());
                item.setCustomMessage(itemDto.getCustomMessage());
                item.setDetailsRequired(false);
                item.setProductDetails(buildDetailsString(itemDto.getSelectedOptions()));
                items.add(item);
            }
            purchase.setItems(items);

            Purchase saved = purchaseRepository.save(purchase);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    private String buildDetailsString(Map<String, Double> options) {
        if (options == null || options.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> entry : options.entrySet()) {
            sb.append(entry.getKey())
                    .append(": +")
                    .append(entry.getValue())
                    .append("€; ");
        }
        return sb.toString();
    }

    // DTOs con @Getter y @Setter
    public static class PurchaseRequest {
        @Getter @Setter
        private List<CartItemDto> items;
        @Getter @Setter
        private double total;
        @Getter @Setter
        private CustomerDto customer;
        @Getter @Setter
        private String paymentMethod;
    }

    public static class CartItemDto {
        @Getter @Setter
        private int productId;
        @Getter @Setter
        private double basePrice;
        @Getter @Setter
        private double totalPrice;
        @Getter @Setter
        private Map<String, Double> selectedOptions;
        @Getter @Setter
        private String customMessage;
    }

    public static class CustomerDto {
        @Getter @Setter
        private String name;
        @Getter @Setter
        private String email;
        @Getter @Setter
        private String address;
    }
}