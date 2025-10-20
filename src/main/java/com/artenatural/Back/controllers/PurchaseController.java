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

        String token = authHeader.substring(7);
        String userId = jwtTokenUtil.getUserIdFromToken(token);
        User user = userRepository.findById(Integer.parseInt(userId)).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        // Crear la compra
        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setStatus(Purchase.Status.COMPLETED); // o PROCESSING si prefieres
        purchase.setTotal(request.getTotal());

        // Guardar datos del cliente (opcional: podrías guardarlos en User si es nuevo)
        // Por ahora, los guardamos como JSON en un campo o en una entidad separada si la tienes
        // Aquí los dejamos en memoria; en producción, crea una entidad `Customer` o `ShippingAddress`

        // Crear los items del carrito
        List<PurchaseItem> items = new ArrayList<>();
        for (CartItemDto itemDto : request.getItems()) {
            PurchaseItem item = new PurchaseItem();
            item.setPurchase(purchase);
            item.setProduct(productRepository.findById(itemDto.getProductId()).orElse(null));
            item.setQuantity(1); // asumiendo 1 unidad por producto
            item.setPrice(itemDto.getBasePrice());
            item.setTotal(itemDto.getTotalPrice());
            item.setDetailsRequired(false); // ajusta si necesitas detalles
            item.setProductDetails(buildDetailsString(itemDto.getSelectedOptions()));
            items.add(item);
        }

        purchase.setItems(items);
        Purchase saved = purchaseRepository.save(purchase);
        return ResponseEntity.ok(saved);
    }

    private String buildDetailsString(Map<String, Double> options) {
        if (options == null || options.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> entry : options.entrySet()) {
            sb.append(entry.getKey())
                    .append(": +")
                    .append(entry.getValue())
                    .append("€; ");
        }
        return sb.toString();
    }

    // DTO principal
    public static class PurchaseRequest {
        private List<CartItemDto> items;
        private double total;
        private CustomerDto customer;
        private String paymentMethod;

        // Getters y setters
        public List<CartItemDto> getItems() { return items; }
        public void setItems(List<CartItemDto> items) { this.items = items; }
        public double getTotal() { return total; }
        public void setTotal(double total) { this.total = total; }
        public CustomerDto getCustomer() { return customer; }
        public void setCustomer(CustomerDto customer) { this.customer = customer; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }

    // DTO para cada item del carrito
    public static class CartItemDto {
        private int productId;
        private double basePrice;
        private double totalPrice;
        private Map<String, Double> selectedOptions; // Ej: {"Tamaño": 10.0, "Color": 5.0}

        // Getters y setters
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }
        public double getBasePrice() { return basePrice; }
        public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
        public double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
        public Map<String, Double> getSelectedOptions() { return selectedOptions; }
        public void setSelectedOptions(Map<String, Double> selectedOptions) { this.selectedOptions = selectedOptions; }
    }

    // DTO para datos del cliente
    public static class CustomerDto {
        private String name;
        private String email;
        private String address;

        // Getters y setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }
}