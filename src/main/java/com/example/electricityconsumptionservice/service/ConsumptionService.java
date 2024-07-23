package com.example.electricityconsumptionservice.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConsumptionService {

    private List<Double> dailyConsumptions = new ArrayList<>();
    private List<String> dailyTrends = new ArrayList<>();
    private boolean apiAvailable = false;

    public boolean isApiAvailable() {
        return apiAvailable;
    }

    public double getDailyConsumption() {
        // Implémentez l'appel API ici pour obtenir la consommation quotidienne
        return 0;
    }

    public List<Double> getDailyConsumptions() {
        return dailyConsumptions;
    }

    public List<String> getDailyTrends() {
        return dailyTrends;
    }

    public void saveConsumptionData(List<String[]> data) {
        dailyConsumptions.clear();
        dailyTrends.clear();

        for (String[] row : data) {
            if (row.length >= 2) {
                try {
                    double consumption = Double.parseDouble(row[1]);
                    dailyConsumptions.add(consumption);

                    if (dailyConsumptions.size() > 1) {
                        double previousConsumption = dailyConsumptions.get(dailyConsumptions.size() - 2);
                        if (consumption > previousConsumption) {
                            dailyTrends.add("increase");
                        } else if (consumption < previousConsumption) {
                            dailyTrends.add("decrease");
                        } else {
                            dailyTrends.add("no change");
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid number format
                }
            }
        }
    }
}
