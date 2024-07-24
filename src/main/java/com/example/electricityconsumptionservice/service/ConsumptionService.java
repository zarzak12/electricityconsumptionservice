package com.example.electricityconsumptionservice.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConsumptionService {

    private List<Double> dailyConsumptions = new ArrayList<>();
    private List<String> trends = new ArrayList<>();
    private List<String> dates = new ArrayList<>();
    private double monthlyPayment;

    public boolean isApiAvailable() {
        return false;
    }

    public List<Double> getDailyConsumptions() {
        return dailyConsumptions;
    }

    public List<String> getDailyTrends() {
        return trends;
    }

    public List<String> getConsumptionDates() {
        return dates;
    }

    public void saveConsumptionData(List<String[]> data) {
        dailyConsumptions.clear();
        trends.clear();
        dates.clear();

        boolean isFirstTwoLines = true; // Ignorer les deux premières lignes
        for (String[] line : data) {
            if (isFirstTwoLines) {
                if (data.indexOf(line) < 2) {
                    continue;
                }
                isFirstTwoLines = false;
            }

            if (line.length < 2) continue;
            dates.add(line[0]);
            double consumption = Double.parseDouble(line[1].replace(",", "."));
            dailyConsumptions.add(consumption);
        }
        calculateTrends();
    }

    private void calculateTrends() {
        double averageConsumption = calculateAverageConsumption();

        for (Double consumption : dailyConsumptions) {
            if (consumption > averageConsumption) {
                trends.add("increase");
            } else if (consumption < averageConsumption) {
                trends.add("decrease");
            } else {
                trends.add("no change");
            }
        }
    }

    public void setMonthlyPayment(double monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public double calculateAverageConsumption() {
        double tarifBleu = 0.1546; // Example rate per kWh
        double totalMonthlyConsumption = monthlyPayment / tarifBleu;
        return totalMonthlyConsumption / 30; // average daily consumption
    }

    public Map<String, Double> getMonthlyConsumptions() {
        Map<String, Double> monthlyConsumptions = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (int i = 0; i < dates.size(); i++) {
            LocalDate date = LocalDate.parse(dates.get(i), formatter);
            String month = date.getMonth().toString() + " " + date.getYear();
            double consumption = dailyConsumptions.get(i);

            monthlyConsumptions.put(month, monthlyConsumptions.getOrDefault(month, 0.0) + consumption);
        }

        return monthlyConsumptions;
    }

    public Map<String, String> getMonthlyTrends() {
        Map<String, Double> monthlyConsumptions = getMonthlyConsumptions();
        Map<String, String> monthlyTrends = new LinkedHashMap<>();
        double averageMonthlyConsumption = calculateAverageConsumption() * 30; // Average monthly consumption

        for (String month : monthlyConsumptions.keySet()) {
            double consumption = monthlyConsumptions.get(month);
            if (consumption > averageMonthlyConsumption) {
                monthlyTrends.put(month, "increase");
            } else {
                monthlyTrends.put(month, "decrease");
            }
        }

        return monthlyTrends;
    }
}
