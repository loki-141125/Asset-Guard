package com.assetguard.app.controller;

import com.assetguard.app.model.Asset;
import com.assetguard.app.model.User;
import com.assetguard.app.service.AssetService;
import com.assetguard.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private AssetService assetService;

    @Autowired
    private UserService userService;

    private User getCurrentUser(Principal principal) {
        if (principal == null)
            return null;
        return userService.findByEmail(principal.getName()).orElse(null);
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        List<Asset> assets = assetService.getAssetsByUser(user.getId());
        Double totalValue = assetService.getTotalValueByUser(user.getId());
        Map<String, Object> typeStats = assetService.getStatsByTypeForUser(user.getId());
        Map<String, Long> statusCounts = assetService.getStatusCountsForUser(user.getId());

        long activeCount = statusCounts.getOrDefault("ACTIVE", 0L);
        long maintenanceCount = statusCounts.getOrDefault("MAINTENANCE", 0L);

        model.addAttribute("user", user);
        model.addAttribute("assets", assets);
        model.addAttribute("totalValue", totalValue);
        model.addAttribute("assetCount", assets.size());
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("maintenanceCount", maintenanceCount);
        model.addAttribute("typeStats", typeStats);
        model.addAttribute("newAsset", new Asset());

        return "dashboard";
    }

    @PostMapping("/assets/add")
    public String addAsset(@ModelAttribute Asset asset, Principal principal, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            if (asset.getPurchaseDate() == null) {
                asset.setPurchaseDate(LocalDate.now());
            }
            assetService.createAsset(asset, user.getId());
            redirectAttributes.addFlashAttribute("success", "Asset added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add asset: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/assets/edit/{id}")
    public String editAsset(@PathVariable Long id, @ModelAttribute Asset asset,
            Principal principal, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            assetService.updateAsset(id, asset, user.getId());
            redirectAttributes.addFlashAttribute("success", "Asset updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update asset: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/assets/delete/{id}")
    public String deleteAsset(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            assetService.deleteAsset(id, user.getId());
            redirectAttributes.addFlashAttribute("success", "Asset deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete asset: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }
}
