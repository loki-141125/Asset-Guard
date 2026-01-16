package com.assetguard.app.controller;

import com.assetguard.app.model.Asset;
import com.assetguard.app.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Asset> assets = assetService.getAllAssets();
        Double totalValue = assetService.getTotalValue();

        model.addAttribute("assets", assets);
        model.addAttribute("totalValue", totalValue);
        model.addAttribute("assetCount", assets.size());

        // Setup empty asset for modal
        model.addAttribute("newAsset", new Asset());

        return "dashboard";
    }

    @PostMapping("/assets/add")
    public String addAsset(@ModelAttribute Asset asset) {
        if (asset.getPurchaseDate() == null) {
            asset.setPurchaseDate(LocalDate.now());
        }
        assetService.saveAsset(asset);
        return "redirect:/dashboard";
    }
}
