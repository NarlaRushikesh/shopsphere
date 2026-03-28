package com.example.demo.feign;

import com.example.demo.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "ORDER-SERVICE", configuration = FeignConfig.class)
public interface OrderClient {

    @GetMapping("/all")
    List<Object> getAllOrders();

    @PutMapping("/{id}/status")
    Object updateStatus(@PathVariable Long id, @RequestParam String status);
}