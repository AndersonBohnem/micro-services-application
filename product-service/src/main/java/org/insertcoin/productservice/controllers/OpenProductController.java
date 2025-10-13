package org.insertcoin.productservice.controllers;

import org.insertcoin.productservice.clients.CurrencyClient;
import org.insertcoin.productservice.clients.CurrencyResponse;
import org.insertcoin.productservice.entrities.ProductEntity;
import org.insertcoin.productservice.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("products")
public class OpenProductController {

    private final ProductRepository repository;
    private final CurrencyClient currencyClient;
    private final CacheManager cacheManager;

    public OpenProductController(ProductRepository repository, CurrencyClient currencyClient, CacheManager cacheManager) {
        super();
        this.repository = repository;
        this.currencyClient = currencyClient;
        this.cacheManager = cacheManager;
    }

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/{idProduct}/{targetCurrency}")
    public ResponseEntity<ProductEntity> getProduct(
            @PathVariable Long idProduct,
            @PathVariable String targetCurrency
    ) throws Exception {

        ProductEntity product = repository.findById(idProduct).orElseThrow(() -> new Exception("Product not found"));

        product.setEnviroment("Product-Service running on port: " + serverPort);

        if(targetCurrency.equalsIgnoreCase(product.getCurrency())) {
            product.setConvertedPrice(product.getPrice());
        } else {
            String dataSource = "None";
            String keyCache = idProduct + targetCurrency.toUpperCase();
            String nameCache = "ProductCache";

            CurrencyResponse currency = cacheManager.getCache(nameCache).get(keyCache, CurrencyResponse.class);

            if(currency != null) {
                dataSource = "Cache";
            } else {
                currency = currencyClient.getCurrency(
                        product.getPrice(),
                        product.getCurrency(),
                        targetCurrency
                );

                if (currency.getConvertedValue() != -1) {
                    cacheManager.getCache(nameCache).put(keyCache, currency);
                }

                dataSource = (currency.getConvertedValue() != -1) ? "currency-service" : "Fallback";
            }

            dataSource = "Get Information: " + dataSource;
            product.setConvertedPrice(currency.getConvertedValue());
            product.setEnviroment(product.getEnviroment() + " - " + dataSource + " - " + currency.getEnviroment());
        }

        return ResponseEntity.ok(product);
    }
}
