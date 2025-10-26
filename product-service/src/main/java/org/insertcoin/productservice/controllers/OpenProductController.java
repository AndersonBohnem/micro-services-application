package org.insertcoin.productservice.controllers;

import org.insertcoin.productservice.clients.CurrencyClient;
import org.insertcoin.productservice.clients.CurrencyResponse;
import org.insertcoin.productservice.entrities.ProductEntity;
import org.insertcoin.productservice.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;

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

        String dataSource = "None";
        String keyCache = idProduct + targetCurrency.toUpperCase();
        String nameCache = "ProductCache";

        ProductEntity product = cacheManager.getCache(nameCache).get(keyCache, ProductEntity.class);

        if (product != null) {
            dataSource = "Cache";
        } else {
            product = repository.findById(idProduct)
                    .orElseThrow(() -> new Exception("Product not found"));
            cacheManager.getCache(nameCache).put(keyCache, product);
            dataSource = "Database";

            product.setEnviroment("Product-Service running on port: " + serverPort + " - Source: " + dataSource);

            if (targetCurrency.equalsIgnoreCase(product.getCurrency())) {
                product.setConvertedPrice(product.getPrice());
            } else {
                CurrencyResponse currency = currencyClient.getCurrency(
                        product.getPrice(),
                        product.getCurrency(),
                        targetCurrency
                );

                if (currency.getConvertedValue() == -1) {
                    product.setEnviroment(product.getEnviroment() + " - Using fallback conversion");
                    product.setConvertedPrice(product.getPrice());
                } else {
                    product.setConvertedPrice(currency.getConvertedValue());
                    product.setEnviroment(product.getEnviroment() + " - Currency from: " + currency.getEnviroment());
                }
            }
        }

        return ResponseEntity.ok(product);
    }

    @GetMapping("/noconverter/{idProduct}")
    public ResponseEntity<ProductEntity> getNoConverter(
            @PathVariable Long idProduct
    ) throws Exception {
        var product = repository.findById(idProduct).orElseThrow(() -> new Exception("Produto não encontrado"));
        product.setConvertedPrice(-1);
        product.setEnviroment("Product-Service running on port: " + serverPort);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{targetCurrency}")
    public ResponseEntity<Page<ProductEntity>> getAllProducts(
            @PathVariable String targetCurrency,
            @PageableDefault(page = 0, size = 5, sort = "description", direction = Sort.Direction.ASC) Pageable pageable
    ) throws Exception {
        Page<ProductEntity> products = repository.findAll((org.springframework.data.domain.Pageable) pageable);

        for(ProductEntity product : products) {
            CurrencyResponse currency = currencyClient.getCurrency(product.getPrice(), product.getCurrency(), targetCurrency);

            product.setConvertedPrice(currency.getConvertedValue());

            product.setEnviroment("Product-Service running on port: " + serverPort + " - " + currency.getEnviroment());
        }
        return ResponseEntity.ok(products);
    }


}
