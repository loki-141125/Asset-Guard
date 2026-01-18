package com.assetguard.app.controller;

import com.assetguard.app.model.Asset;
import com.assetguard.app.model.MaintenanceRecord;
import com.assetguard.app.model.User;
import com.assetguard.app.service.AssetService;
import com.assetguard.app.service.MaintenanceService;
import com.assetguard.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/maintenance")
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private UserService userService;

    private User getCurrentUser(Principal principal) {
        if (principal == null)
            return null;
        return userService.findByEmail(principal.getName()).orElse(null);
    }

    @GetMapping
    public String maintenance(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        if (user == null)
            return "redirect:/login";

        List<MaintenanceRecord> records = maintenanceService.getUserMaintenanceRecords(user.getId());
        Double totalCost = maintenanceService.getTotalMaintenanceCost(user.getId());
        List<Asset> assets = assetService.getAssetsByUser(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("records", records);
        model.addAttribute("totalCost", totalCost);
        model.addAttribute("assets", assets);
        model.addAttribute("newRecord", new MaintenanceRecord());

        return "maintenance";
    }

    @PostMapping("/add")
    public String addMaintenance(@ModelAttribute MaintenanceRecord record,
            @RequestParam Long assetId,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        if (user == null)
            return "redirect:/login";

        try {
            maintenanceService.addMaintenanceRecord(record, assetId, user.getId());
            redirectAttributes.addFlashAttribute("success", "Maintenance record added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add record: " + e.getMessage());
        }
        return "redirect:/maintenance";
    }
}
