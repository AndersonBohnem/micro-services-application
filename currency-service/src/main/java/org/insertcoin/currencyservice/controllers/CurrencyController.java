package org.insertcoin.currencyservice.controllers;


import org.insertcoin.currencyservice.clients.CurrencyBCClient;
import org.insertcoin.currencyservice.clients.CurrencyBCResponse;
import org.insertcoin.currencyservice.entities.CurrencyEntity;
import org.insertcoin.currencyservice.repositories.CurrencyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@RestController
@RequestMapping("currency")
public class CurrencyController {
    private final CurrencyRepository repository;
    private final CurrencyBCClient currencyBCClient;
    private final CacheManager cacheManager;

    @Value("${server.port}")
    private int serverPort;

    public CurrencyController(CurrencyRepository repository, CurrencyBCClient currencyBCClient, CacheManager cacheManager) {
        super();
        this.repository = repository;
        this.currencyBCClient = currencyBCClient;
        this.cacheManager = cacheManager;
    }

    @GetMapping("/{value}/{source}/{target}")
    public ResponseEntity<CurrencyEntity> getConversion(
            @PathVariable double value,
            @PathVariable String source,
            @PathVariable String target
    ) throws Exception {
        source = source.toUpperCase();
        target = target.toUpperCase();

        String dataSource = "None";
        String keyCache = source + target;
        String nameCache = "CurrencyCache";

        CurrencyEntity currency = cacheManager.getCache(nameCache).get(keyCache, CurrencyEntity.class);

        if(currency != null) {
            dataSource = "Cache";
        } else {
            currency = new CurrencyEntity();
            currency.setSource(source);
            currency.setTarget(target);

            if(source.equals(target)) {
                currency.setConversionRate(1);
            } else {
                try {
                    double sourceRate = 1;
                    double targetRate = 1;
                    if(!source.equals("BRL")) {
                        CurrencyBCResponse response = getLastQuoteBC(source);
                        if(response.getValue().isEmpty()) {
                            throw new Exception();
                        }
                        sourceRate = response.getValue().get(
                                response.getValue().size() - 1).getCotacaoVenda();
                    }
                    if(!target.equals("BRL")) {
                        CurrencyBCResponse response = getLastQuoteBC(source);
                        if(response.getValue().isEmpty()) {
                            throw new Exception();
                        }
                        targetRate = response.getValue().get(
                                response.getValue().size() - 1).getCotacaoVenda();
                    }
                    currency.setConversionRate(sourceRate / targetRate);
                    dataSource = "API BCB";
                } catch (Exception e) {
                    currency = repository.findBySourceAndTarget(source,target)
                            .orElseThrow(() -> new Exception("Currency not found"));
                    dataSource = "Local Database";
                }
            }
            cacheManager.getCache(nameCache).put(keyCache, currency);
        }

        currency.setConvertedValue(value * currency.getConversionRate());
        currency.setEnviroment("Currency running in port: " + serverPort + " - " + dataSource);

        return ResponseEntity.ok(currency);
    }

    private CurrencyBCResponse getLastQuoteBC(String moeda) {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

        int max_attempts = 3;
        int attempts = 0;

        while (attempts < max_attempts) {
            while (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                date = date.minusDays(1);
            }

            String formattedDate = formatter.format(date);

            try {
                CurrencyBCResponse response = currencyBCClient.getCurrencyBC(moeda, formattedDate);

                if (response.getValue() != null && !response.getValue().isEmpty()) {
                    return response;
                } else {
                    date = date.minusDays(1);
                    attempts++;
                }

            } catch (Exception e) {
                date = date.minusDays(1);
                attempts++;
            }
        }
        CurrencyBCResponse fallback = new CurrencyBCResponse();
        fallback.setValue(Collections.emptyList());
        return fallback;
    }

}
