package com.assetguard.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.persistence.*; // <--- UPDATED: Using Jakarta JPA tools
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
    public Asset add(@RequestBody Asset asset) { return repo.save(asset); }
}

// --- UPDATED DATA MODEL (JPA Version) ---
@Entity
@Table(name = "assets") // Matches your TiDB table name
class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String serialNumber;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
}

interface AssetRepository extends CrudRepository<Asset, Long> {}