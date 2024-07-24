package com.example.electricityconsumptionservice.controller;

import com.example.electricityconsumptionservice.service.ConsumptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class ConsumptionController {

    @Autowired
    private ConsumptionService consumptionService;

    private static final Logger logger = Logger.getLogger(ConsumptionController.class.getName());

    @GetMapping("/consumption")
    public String getConsumption(Model model) {
        boolean apiAvailable = consumptionService.isApiAvailable();
        model.addAttribute("apiUnavailable", !apiAvailable);

        List<Double> dailyConsumptions = consumptionService.getDailyConsumptions();
        List<String> trends = consumptionService.getDailyTrends();
        List<String> dates = consumptionService.getConsumptionDates();
        logger.info("Daily Consumptions: " + dailyConsumptions);
        logger.info("Trends: " + trends);
        model.addAttribute("dailyConsumptions", dailyConsumptions);
        model.addAttribute("trends", trends);
        model.addAttribute("dates", dates);

        return "consumption";
    }

    @PostMapping("/upload-csv")
    public String uploadCSV(@RequestParam("file") MultipartFile file, Model model) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (lineNumber <= 2) continue; // Ignorer les deux premières lignes
                String[] values = line.split(";");
                data.add(values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        consumptionService.saveConsumptionData(data);
        logger.info("Data uploaded: " + data);
        return "redirect:/consumption";
    }
}
