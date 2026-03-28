package com.example.demo.event;

import java.io.Serializable;

public class OrderEvent implements Serializable {

    private Long orderId;
    private String userEmail;
    private double totalAmount;
    private String status;

    public OrderEvent() {}

    public Long getOrderId()                    { return orderId; }
    public void setOrderId(Long orderId)        { this.orderId = orderId; }

    public String getUserEmail()                { return userEmail; }
    public void setUserEmail(String userEmail)  { this.userEmail = userEmail; }

    public double getTotalAmount()                  { return totalAmount; }
    public void setTotalAmount(double totalAmount)  { this.totalAmount = totalAmount; }

    public String getStatus()               { return status; }
    public void setStatus(String status)    { this.status = status; }
}