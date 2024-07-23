package com.example.electricityconsumptionservice.controller;

import com.example.electricityconsumptionservice.service.ConsumptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConsumptionController.class)
public class ConsumptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsumptionService consumptionService;

    private List<Double> dailyConsumptions;
    private List<String> trends;

    @BeforeEach
    public void setUp() {
        dailyConsumptions = Arrays.asList(12.0, 17.0, 19.0, 15.0, 14.0);
        trends = Arrays.asList("increase", "increase", "decrease", "decrease");
    }

    @Test
    public void testGetConsumption() throws Exception {
        given(consumptionService.isApiAvailable()).willReturn(false);
        given(consumptionService.getDailyConsumptions()).willReturn(dailyConsumptions);
        given(consumptionService.getDailyTrends()).willReturn(trends);

        mockMvc.perform(get("/consumption"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("apiUnavailable", true))
                .andExpect(model().attribute("dailyConsumptions", dailyConsumptions))
                .andExpect(model().attribute("trends", trends))
                .andExpect(view().name("consumption"))
                .andExpect(content().string(containsString("Consommation Quotidienne")));
    }
}
