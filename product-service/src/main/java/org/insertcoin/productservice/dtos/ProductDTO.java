package org.insertcoin.productservice.dtos;

public record ProductDTO(String description,
                         String brand,
                         String model,
                         String currency,
                         double price,
                         String imageUrl) {
}
