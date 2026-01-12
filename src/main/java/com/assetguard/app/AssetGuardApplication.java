package com.assetguard.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.persistence.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@Controller
public class AssetGuardApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssetGuardApplication.class, args);
    }

    @Autowired AssetRepository repo;

    @GetMapping("/")
    public String home() { return "home"; }

    @GetMapping("/api/assets")
    @ResponseBody
    public Iterable<Asset> getAll() { return repo.findAll(); }

    @PostMapping("/api/add")
    @ResponseBody
    public Asset add(@RequestBody Asset asset) { 
        // Default status if empty
        if(asset.getStatus() == null) asset.setStatus("Available");
        return repo.save(asset); 
    }

    @DeleteMapping("/api/delete/{id}")
    @ResponseBody
    public void delete(@PathVariable Long id) { repo.deleteById(id); }
    
    // NEW: Quick Status Update (e.g., Move to Repair)
    @PutMapping("/api/update/{id}")
    @ResponseBody
    public Asset updateStatus(@PathVariable Long id, @RequestBody Asset details) {
        Asset asset = repo.findById(id).orElse(null);
        if(asset != null) {
            asset.setStatus(details.getStatus());
            asset.setAssignedTo(details.getAssignedTo());
            return repo.save(asset);
        }
        return null;
    }
}

@Entity
@Table(name = "assets")
class Asset {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String serialNumber;
    private String assignedTo; // Employee Name
    private String status;     // Available, In Use, In Repair

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

interface AssetRepository extends CrudRepository<Asset, Long> {}