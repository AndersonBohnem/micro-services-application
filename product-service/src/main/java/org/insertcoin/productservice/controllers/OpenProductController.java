package org.insertcoin.productservice.controllers;

import org.insertcoin.productservice.entrities.ProductEntity;
import org.insertcoin.productservice.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("products")
public class OpenProductController {

    private final ProductRepository repository;

    public OpenProductController(ProductRepository repository) {
        super();
        this.repository = repository;
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
        product.setConvertedPrice(product.getPrice()); // MOCK para teste

        return ResponseEntity.ok(product);
    }
}
