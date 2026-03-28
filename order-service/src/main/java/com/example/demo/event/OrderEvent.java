package com.example.demo.event;

import java.io.Serializable;

public class OrderEvent implements Serializable {

    private Long orderId;
    private String userEmail;
    private double totalAmount;
    private String status;

    public OrderEvent() {}

    public OrderEvent(Long orderId, String userEmail, double totalAmount, String status) {
        this.orderId     = orderId;
        this.userEmail   = userEmail;
        this.totalAmount = totalAmount;
        this.status      = status;
    }

    public Long getOrderId()          { return orderId; }
    public void setOrderId(Long id)   { this.orderId = id; }

    public String getUserEmail()             { return userEmail; }
    public void setUserEmail(String email)   { this.userEmail = email; }

    public double getTotalAmount()              { return totalAmount; }
    public void setTotalAmount(double amount)   { this.totalAmount = amount; }

    public String getStatus()            { return status; }
    public void setStatus(String status) { this.status = status; }
}