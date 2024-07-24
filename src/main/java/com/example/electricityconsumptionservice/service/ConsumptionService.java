package com.example.electricityconsumptionservice.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

        double averageConsumption = calculateAverageConsumption();

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

            if (dailyConsumptions.size() > 1) {
                trends.add(consumption > averageConsumption ? "increase" : "decrease");
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
}
