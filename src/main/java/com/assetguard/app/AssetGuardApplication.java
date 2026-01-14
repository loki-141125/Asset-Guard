package com.assetguard.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.*;

@SpringBootApplication
public class AssetGuardApplication {
    public static void main(String[] args) {
        SpringApplication.run(AssetGuardApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public org.springframework.web.filter.CorsFilter corsFilter() {
        org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new org.springframework.web.filter.CorsFilter(source);
    }
}

// --- REST API CONTROLLER ---
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
class ApiController {

    @Autowired private AssetRepository assetRepo;
    @Autowired private HistoryRepository historyRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private DepartmentRepository deptRepo;
    @Autowired private CategoryRepository categoryRepo;
    @Autowired private MaintenanceRepository maintenanceRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    // --- AUTHENTICATION ENDPOINTS ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO dto) {
        if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(new ApiResponse("Email already exists"));
        }
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("ROLE_USER");
        user.setDepartmentId(dto.getDepartmentId());
        userRepo.save(user);
        return ResponseEntity.ok(new ApiResponse("Registration successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        Optional<User> userOpt = userRepo.findByEmail(dto.getEmail());
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Invalid credentials"));
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Invalid credentials"));
        }
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("fullName", user.getFullName());
        response.put("role", user.getRole());
        response.put("departmentId", user.getDepartmentId());
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    // --- ASSET ENDPOINTS ---
    @GetMapping("/assets")
    public List<Asset> getAssets(@RequestParam(required = false) String status, 
                                  @RequestParam(required = false) Long categoryId,
                                  @RequestParam(required = false) String search) {
        List<Asset> assets = new ArrayList<>();
        assetRepo.findAll().forEach(assets::add);
        
        if (status != null && !status.isEmpty()) {
            assets = assets.stream().filter(a -> a.getStatus().equals(status)).toList();
        }
        if (categoryId != null) {
            assets = assets.stream().filter(a -> a.getCategoryId() != null && a.getCategoryId().equals(categoryId)).toList();
        }
        if (search != null && !search.isEmpty()) {
            String searchLower = search.toLowerCase();
            assets = assets.stream().filter(a -> a.getName().toLowerCase().contains(searchLower) || 
                                                   a.getSerialNumber().toLowerCase().contains(searchLower)).toList();
        }
        return assets;
    }

    @GetMapping("/assets/{id}")
    public ResponseEntity<?> getAsset(@PathVariable(value = "id") Long id) {
        if (id == null) return ResponseEntity.badRequest().build();
        return assetRepo.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/assets")
    public ResponseEntity<?> addAsset(@RequestBody Asset asset) {
        if (asset.getName() == null || asset.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("Asset name is required"));
        }
        if (asset.getStatus() == null) asset.setStatus("Available");
        asset.setCreatedDate(new Date());
        Asset saved = assetRepo.save(asset);
        log(saved.getName(), "Asset Added", asset.getCreatedBy());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/assets/{id}")
    public ResponseEntity<?> updateAsset(@PathVariable(value = "id") Long id, @RequestBody Asset data) {
        if (id == null) return ResponseEntity.badRequest().build();
        Optional<Asset> opt = assetRepo.findById(id);
        if (!opt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Asset asset = opt.get();
        if (data.getName() != null) asset.setName(data.getName());
        if (data.getSerialNumber() != null) asset.setSerialNumber(data.getSerialNumber());
        if (data.getStatus() != null) {
            String oldStatus = asset.getStatus();
            asset.setStatus(data.getStatus());
            log(asset.getName(), "Status: " + oldStatus + " → " + data.getStatus(), data.getModifiedBy());
        }
        if (data.getAssignedTo() != null) {
            asset.setAssignedTo(data.getAssignedTo());
            log(asset.getName(), "Assigned to: " + data.getAssignedTo(), data.getModifiedBy());
        }
        if (data.getCategoryId() != null) asset.setCategoryId(data.getCategoryId());
        if (data.getLocation() != null) asset.setLocation(data.getLocation());
        if (data.getPrice() != null) asset.setPrice(data.getPrice());
        asset.setModifiedDate(new Date());
        Asset updated = assetRepo.save(asset);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/assets/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable(value = "id") Long id) {
        if (id == null) return ResponseEntity.badRequest().build();
        Optional<Asset> opt = assetRepo.findById(id);
        if (!opt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Asset asset = opt.get();
        assetRepo.deleteById(id);
        log(asset.getName(), "Asset Deleted", null);
        return ResponseEntity.ok(new ApiResponse("Asset deleted successfully"));
    }

    // --- STATISTICS ENDPOINTS ---
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", assetRepo.count());
        stats.put("active", assetRepo.countByStatus("In Use"));
        stats.put("repair", assetRepo.countByStatus("Repair"));
        stats.put("available", assetRepo.countByStatus("Available"));
        stats.put("deprecated", assetRepo.countByStatus("Deprecated"));
        Double totalValue = assetRepo.sumPrice();
        stats.put("totalValue", totalValue != null ? totalValue : 0.0);
        return ResponseEntity.ok(stats);
    }

    // --- HISTORY/AUDIT ENDPOINTS ---
    @GetMapping("/history")
    public List<History> getHistory(@RequestParam(required = false, defaultValue = "50") int limit) {
        return historyRepo.findTopByOrderByTimestampDesc(limit);
    }

    // --- DEPARTMENT ENDPOINTS ---
    @GetMapping("/departments")
    public Iterable<Department> getDepartments() {
        return deptRepo.findAll();
    }

    @PostMapping("/departments")
    public ResponseEntity<?> addDepartment(@RequestBody Department dept) {
        if (dept.getName() == null || dept.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("Department name is required"));
        }
        return ResponseEntity.ok(deptRepo.save(dept));
    }

    // --- CATEGORY ENDPOINTS ---
    @GetMapping("/categories")
    public Iterable<Category> getCategories() {
        return categoryRepo.findAll();
    }

    @PostMapping("/categories")
    public ResponseEntity<?> addCategory(@RequestBody Category cat) {
        if (cat.getName() == null || cat.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("Category name is required"));
        }
        return ResponseEntity.ok(categoryRepo.save(cat));
    }

    // --- MAINTENANCE ENDPOINTS ---
    @GetMapping("/maintenance")
    public Iterable<Maintenance> getMaintenance(@RequestParam(required = false) Long assetId) {
        if (assetId != null) {
            return maintenanceRepo.findByAssetId(assetId);
        }
        return maintenanceRepo.findAll();
    }

    @PostMapping("/maintenance")
    public ResponseEntity<?> addMaintenance(@RequestBody Maintenance maintenance) {
        if (maintenance.getAssetId() == null) {
            return ResponseEntity.badRequest().body(new ApiResponse("Asset ID is required"));
        }
        maintenance.setDate(new Date());
        Maintenance saved = maintenanceRepo.save(maintenance);
        Long assetId = maintenance.getAssetId();
        Asset asset = (assetId != null) ? assetRepo.findById(assetId).orElse(null) : null;
        if (asset != null) {
            log(asset.getName(), "Maintenance: " + maintenance.getType(), maintenance.getPerformedBy());
        }
        return ResponseEntity.ok(saved);
    }

    // --- UTILITY METHODS ---
    private void log(String assetName, String action, String user) {
        History h = new History();
        h.setAssetName(assetName);
        h.setAction(action);
        h.setUser(user != null ? user : "System");
        h.setTimestamp(new Date());
        historyRepo.save(h);
    }
}

// --- HTML PAGE CONTROLLER ---
@org.springframework.stereotype.Controller
class PageController {
    @GetMapping("/") public String index() { return "index"; }
    @GetMapping("/login") public String login() { return "login"; }
    @GetMapping("/register") public String register() { return "register"; }
    @GetMapping("/dashboard") public String dashboard() { return "dashboard"; }
}

// --- DTOs ---
class UserDTO {
    public String email;
    public String password;
    public String fullName;
    public Long departmentId;
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
}

class LoginDTO {
    public String email;
    public String password;
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

class ApiResponse {
    public String message;
    public ApiResponse(String msg) { this.message = msg; }
}

// --- DATABASE ENTITIES ---
@Entity @Table(name = "users")
class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String email;
    private String fullName;
    private String password;
    private String role;
    private Long departmentId;
    private boolean active = true;
    private Date createdDate = new Date();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
}

@Entity @Table(name = "assets")
class Asset {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String name;
    private String serialNumber;
    private String assignedTo;
    private String status;
    private Long categoryId;
    private String location;
    private Double price;
    private String createdBy;
    private String modifiedBy;
    private Date createdDate;
    private Date modifiedDate;

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
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(String modifiedBy) { this.modifiedBy = modifiedBy; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public Date getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(Date modifiedDate) { this.modifiedDate = modifiedDate; }
}

@Entity @Table(name = "history")
class History {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String assetName;
    private String action;
    private String user;
    private Date timestamp;

    // Getters and Setters
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

@Entity @Table(name = "departments")
class Department {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String name;
    private String description;
    private String head;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getHead() { return head; }
    public void setHead(String head) { this.head = head; }
}

@Entity @Table(name = "categories")
class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String name;
    private String description;
    private String color;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}

@Entity @Table(name = "maintenance")
class Maintenance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long assetId;
    private String type;
    private String description;
    private Double cost;
    private String performedBy;
    private Date date;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }
    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
}

// --- REPOSITORIES ---
interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

interface AssetRepository extends CrudRepository<Asset, Long> {
    long countByStatus(String status);
    
    @Query("SELECT COALESCE(SUM(a.price), 0) FROM Asset a")
    Double sumPrice();
}

interface HistoryRepository extends CrudRepository<History, Long> {
    List<History> findTopByOrderByTimestampDesc(int limit);
}

interface DepartmentRepository extends CrudRepository<Department, Long> {
}

interface CategoryRepository extends CrudRepository<Category, Long> {
}

interface MaintenanceRepository extends CrudRepository<Maintenance, Long> {
    List<Maintenance> findByAssetId(Long assetId);
}