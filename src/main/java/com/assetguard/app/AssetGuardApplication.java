package com.assetguard.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.*;
import org.springframework.data.repository.CrudRepository;
import java.util.*;

@SpringBootApplication
@RestController
public class AssetGuardApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssetGuardApplication.class, args);
    }

    // --- BACKEND API ---
    @Autowired AssetRepository assets;
    @Autowired HistoryRepository history;

    @GetMapping("/api/assets")
    public Iterable<Asset> getAssets() { return assets.findAll(); }

    @GetMapping("/api/history")
    public Iterable<History> getHistory() { return history.findAllByOrderByTimestampDesc(); }

    @GetMapping("/api/stats")
    public Map<String, Long> getStats() {
        Map<String, Long> map = new HashMap<>();
        map.put("total", assets.count());
        map.put("active", assets.countByStatus("In Use"));
        map.put("repair", assets.countByStatus("Repair"));
        map.put("available", assets.countByStatus("Available"));
        return map;
    }

    @PostMapping("/api/add")
    public Asset add(@RequestBody Asset a) {
        if(a.getStatus() == null) a.setStatus("Available");
        log(a.getName(), "Added Asset", "System");
        return assets.save(a);
    }

    @PutMapping("/api/update/{id}")
    public Asset update(@PathVariable Long id, @RequestBody Asset d) {
        Asset a = assets.findById(id).orElse(null);
        if(a != null) {
            a.setStatus(d.getStatus());
            a.setAssignedTo(d.getAssignedTo());
            log(a.getName(), "Status: " + d.getStatus(), d.getAssignedTo());
            return assets.save(a);
        }
        return null;
    }

    @DeleteMapping("/api/delete/{id}")
    public void delete(@PathVariable Long id) { 
        assets.deleteById(id); 
    }

    private void log(String asset, String action, String user) {
        History h = new History();
        h.setAssetName(asset);
        h.setAction(action);
        h.setUser(user != null ? user : "System");
        h.setTimestamp(new Date());
        history.save(h);
    }
}

// --- HTML PAGE CONTROLLER ---
@org.springframework.stereotype.Controller
class PageController {
    @GetMapping("/") public String index() { return "index"; }
    @GetMapping("/login") public String login() { return "login"; }
}

// --- DATABASE TABLES ---
@Entity @Table(name="assets")
class Asset {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String name;
    private String serialNumber;
    private String assignedTo;
    private String status;
    
    // Getters and Setters
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

@Entity @Table(name="history")
class History {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String assetName;
    private String action;
    private String user;
    private Date timestamp;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}

// --- DATA REPOSITORIES ---
interface AssetRepository extends CrudRepository<Asset, Long> {
    long countByStatus(String status);
}
interface HistoryRepository extends CrudRepository<History, Long> {
    List<History> findAllByOrderByTimestampDesc();
}