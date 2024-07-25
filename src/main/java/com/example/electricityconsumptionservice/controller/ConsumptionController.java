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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        Double averageConsumption = consumptionService.calculateAverageConsumption();
        model.addAttribute("dailyConsumptions", dailyConsumptions);
        model.addAttribute("trends", trends);
        model.addAttribute("dates", dates);
        model.addAttribute("averageConsumption", averageConsumption);

        model.addAttribute("monthlyConsumptions", consumptionService.getMonthlyConsumptions());
        model.addAttribute("monthlyTrends", consumptionService.getMonthlyTrends());

        // Calculer le montant annuel et l'ajouter au modèle
        double annualAmount = consumptionService.calculateAnnualConsumptionEDF();
        model.addAttribute("annualAmount", annualAmount);

        // Ajouter les prix quotidiens au modèle
        model.addAttribute("dailyPrices", consumptionService.getConsumptionPrices());

        // Calcul des prix quotidiens
        List<Double> prices = new ArrayList<>();
        for (double consumption : dailyConsumptions) {
            double price = calculateDailyPriceConsumption(consumption);
            prices.add(price);
        }

        // Calcul des totaux mensuels
        Map<String, Double> monthlyConsumptions = new LinkedHashMap<>();
        Map<String, Double> monthlyPrices = new LinkedHashMap<>();
        Map<String, String> monthlyTrends = new LinkedHashMap<>();
        Map<String, List<Double>> monthlyConsumptionsMap = new LinkedHashMap<>();
        Map<String, List<Double>> monthlyPricesMap = new LinkedHashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (int i = 0; i < dates.size(); i++) {
            LocalDate date = LocalDate.parse(dates.get(i), formatter);
            String month = date.getMonth().name() + " " + date.getYear();

            monthlyConsumptionsMap.putIfAbsent(month, new ArrayList<>());
            monthlyPricesMap.putIfAbsent(month, new ArrayList<>());

            monthlyConsumptionsMap.get(month).add(dailyConsumptions.get(i));
            monthlyPricesMap.get(month).add(prices.get(i));
        }

        for (String month : monthlyConsumptionsMap.keySet()) {
            double totalConsumption = monthlyConsumptionsMap.get(month).stream().mapToDouble(Double::doubleValue).sum();
            double totalPrice = monthlyPricesMap.get(month).stream().mapToDouble(Double::doubleValue).sum();

            double averageConsumptionMonthly = consumptionService.calculateAverageConsumptionMonthly();

            monthlyConsumptions.put(month, totalConsumption);
            monthlyPrices.put(month, totalPrice);

            if (totalConsumption > averageConsumptionMonthly) {
                monthlyTrends.put(month, "increase");
            } else {
                monthlyTrends.put(month, "decrease");
            }
        }

        model.addAttribute("monthlyConsumptions", monthlyConsumptions);
        model.addAttribute("monthlyPrices", monthlyPrices);
        model.addAttribute("monthlyTrends", monthlyTrends);

        model.addAttribute("averageConsumptionMonthly", consumptionService.calculateAverageConsumptionMonthly());

        return "consumption";
    }

    private double calculateDailyPriceConsumption(double dailyConsumption) {
        // Remplacez cette logique par votre calcul de prix réel
        return (dailyConsumption * 0.15) + 0.52;
    }

    @PostMapping("/upload-csv")
    public String uploadCSV(@RequestParam("file") MultipartFile file, @RequestParam("monthlyPayment") double monthlyPayment, Model model) {
        if (file.isEmpty() || !file.getOriginalFilename().endsWith(".csv")) {
            model.addAttribute("errorMessage", "Veuillez télécharger un fichier CSV.");
            return getConsumption(model);
        }

        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 2 && !line.equals("R�capitulatif de ma consommation")) {
                    model.addAttribute("errorMessage", "Le fichier CSV n'est pas au bon format. Veuillez vérifier le contenu et réessayer.");
                    return getConsumption(model);
                }
                if (lineNumber == 4 && !line.equals("Date de consommation;Consommation (kWh);Nature de la donn�e")) {
                    model.addAttribute("errorMessage", "Le fichier CSV n'est pas au bon format. Veuillez vérifier le contenu et réessayer.");
                    return getConsumption(model);
                }
                if (lineNumber > 4) {
                    String[] values = line.split(";");
                    if (values.length < 3 && !line.equals("")) {
                        model.addAttribute("errorMessage", "Le fichier CSV n'est pas au bon format. Chaque ligne doit contenir au moins 3 colonnes.");
                        return getConsumption(model);
                    }
                    data.add(values);
                }
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Une erreur est survenue lors de la lecture du fichier CSV. Veuillez réessayer.");
            e.printStackTrace();
            return getConsumption(model);
        }

        try {
            consumptionService.setMonthlyPayment(monthlyPayment);
            consumptionService.saveConsumptionData(data);
            logger.info("Data uploaded: " + data);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return getConsumption(model);
        }

        // Calculer le montant annuel et l'ajouter au modèle
        double annualAmount = consumptionService.calculateAnnualConsumptionEDF();
        model.addAttribute("annualAmount", annualAmount);

        return "redirect:/consumption";
    }
}
