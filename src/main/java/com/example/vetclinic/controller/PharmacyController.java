package com.example.vetclinic.controller;

import com.example.vetclinic.entity.PharmacyItem;
import com.example.vetclinic.entity.PharmacyOrder;
import com.example.vetclinic.entity.Role;
import com.example.vetclinic.entity.User;
import com.example.vetclinic.repository.PharmacyItemRepository;
import com.example.vetclinic.repository.PharmacyOrderRepository;
import com.example.vetclinic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/pharmacy")
public class PharmacyController {

    @Autowired
    private PharmacyItemRepository pharmacyItemRepository;

    @Autowired
    private UserRepository userRepository;

    // 🛍 Посмотреть список лекарств (все пользователи)
    @GetMapping
    public ResponseEntity<?> getAll() {
        List<PharmacyItem> items = pharmacyItemRepository.findAll();
        return ResponseEntity.ok(items);
    }

    // ➕ Добавить лекарство (только админ)
    @PostMapping
    public ResponseEntity<?> addItem(@RequestBody PharmacyItem item, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body("Only admins can add items");
        }

        return ResponseEntity.ok(pharmacyItemRepository.save(item));
    }

    // 🗑 Удалить лекарство
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body("Only admins can delete items");
        }

        pharmacyItemRepository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }

    // ✏️ Обновить лекарство
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id,
                                        @RequestBody PharmacyItem updatedItem,
                                        Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body("Only admins can update items");
        }

        PharmacyItem item = pharmacyItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setName(updatedItem.getName());
        item.setQuantity(updatedItem.getQuantity());
        item.setPrice(updatedItem.getPrice());
        item.setDescription(updatedItem.getDescription());

        return ResponseEntity.ok(pharmacyItemRepository.save(item));
    }

    @Autowired
    private PharmacyOrderRepository orderRepository;

    @PostMapping("/order")
    public ResponseEntity<?> orderItem(@RequestParam Long itemId,
                                       @RequestParam int quantity,
                                       @RequestParam String action,  // "BUY" или "RESERVE"
                                       Principal principal) {
        User client = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        PharmacyItem item = pharmacyItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (item.getQuantity() < quantity) {
            return ResponseEntity.badRequest().body("Not enough in stock");
        }

        // если это покупка — уменьшаем количество
        if (action.equalsIgnoreCase("BUY")) {
            item.setQuantity(item.getQuantity() - quantity);
            pharmacyItemRepository.save(item);
        }

        PharmacyOrder order = new PharmacyOrder();
        order.setClient(client);
        order.setItem(item);
        order.setQuantity(quantity);
        order.setAction(action.toUpperCase());

        return ResponseEntity.ok(orderRepository.save(order));
    }

    // 📋 Клиент видит свои заказы
    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(Principal principal) {
        User client = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ResponseEntity.ok(orderRepository.findAll()
                .stream()
                .filter(order -> order.getClient().getId().equals(client.getId()))
                .toList());
    }

    // 📋 Админ видит все заказы
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders(Principal principal) {
        User admin = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (admin.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body("Only admins can view all orders");
        }

        return ResponseEntity.ok(orderRepository.findAll());
    }

    // 📦 Лекарства с низким остатком (по умолчанию < 5)
    @GetMapping("/low-stock")
    public ResponseEntity<?> getLowStock(@RequestParam(defaultValue = "5") int threshold,
                                         Principal principal) {
        User admin = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (admin.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body("Only admins can view low stock");
        }

        List<PharmacyItem> lowStockItems = pharmacyItemRepository.findAll()
                .stream()
                .filter(item -> item.getQuantity() < threshold)
                .toList();

        return ResponseEntity.ok(lowStockItems);
    }


}
