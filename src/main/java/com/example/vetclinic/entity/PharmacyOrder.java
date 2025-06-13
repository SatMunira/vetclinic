package com.example.vetclinic.entity;

import jakarta.persistence.*;

@Entity
public class PharmacyOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    private String action; // "RESERVE" или "BUY"

    @ManyToOne
    @JoinColumn(name = "item_id")
    private PharmacyItem item;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public PharmacyItem getItem() { return item; }
    public void setItem(PharmacyItem item) { this.item = item; }

    public User getClient() { return client; }
    public void setClient(User client) { this.client = client; }
}
