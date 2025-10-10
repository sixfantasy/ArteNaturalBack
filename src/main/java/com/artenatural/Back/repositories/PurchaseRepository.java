package com.artenatural.Back.repositories;

import com.artenatural.Back.entities.Purchase;
import com.artenatural.Back.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {
    List<Purchase> findByUser_Id(int userId);

    Purchase findByUser_IdAndStatus(User user, Purchase.Status status);
}
