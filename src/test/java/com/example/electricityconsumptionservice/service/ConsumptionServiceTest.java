// src/test/java/com/example/electricityconsumptionservice/service/ConsumptionServiceTest.java
package com.example.electricityconsumptionservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ConsumptionServiceTest {

    @Autowired
    private ConsumptionService consumptionService;

    @Test
    public void testGetDailyConsumption() {
        double dailyConsumption = consumptionService.getDailyConsumption();
        assertEquals(0.0, dailyConsumption); // Valeur fictive
    }

}
