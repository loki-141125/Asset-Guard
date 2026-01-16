package com.assetguard.app.controller;

import com.assetguard.app.model.Asset;
import com.assetguard.app.model.User;
import com.assetguard.app.service.AssetService;
import com.assetguard.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private AssetService assetService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<Asset> assets = assetService.getAssetsByUser(user);
        Double totalValue = assetService.getTotalValue(user);

        model.addAttribute("user", user);
        model.addAttribute("assets", assets);
        model.addAttribute("totalValue", totalValue);
        model.addAttribute("assetCount", assets.size());

        // Setup empty asset for modal
        model.addAttribute("newAsset", new Asset());

        return "dashboard";
    }

    @PostMapping("/assets/add")
    public String addAsset(@ModelAttribute Asset asset, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        asset.setUser(user);
        if (asset.getPurchaseDate() == null) {
            asset.setPurchaseDate(LocalDate.now());
        }
        assetService.saveAsset(asset);
        return "redirect:/dashboard";
    }
}
