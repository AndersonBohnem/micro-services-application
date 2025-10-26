package org.insertcoin.productservice.controllers;

import com.fasterxml.jackson.databind.util.BeanUtil;
import org.apache.tomcat.websocket.AuthenticationException;
import org.insertcoin.productservice.dtos.ProductDTO;
import org.insertcoin.productservice.entrities.ProductEntity;
import org.insertcoin.productservice.repositories.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ws/products")
public class WsProductControllers {

    private final ProductRepository repository;


    public WsProductControllers(ProductRepository repository) {
        super();
        this.repository = repository;
    }

    private ProductEntity convertDto2Entity(ProductDTO dto) {
        var product = new ProductEntity();
        BeanUtils.copyProperties(dto, product);
        return product;
    }

    @PostMapping
    public ResponseEntity<ProductEntity> post(
            @RequestBody ProductDTO dto,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Email") String userEmail,
            @RequestHeader("X-User-Type") Integer userType
    ) throws Exception{

        if(userType != 0 ) {
            throw new AuthenticationException("Usuário sem premissão");
        }

        var product = convertDto2Entity(dto);
        product.setStock(10);
        repository.save(product);
        return ResponseEntity.status(201).body(product);
    }

    @PutMapping("/{idProduct}")
    public ResponseEntity<ProductEntity> put(
            @PathVariable Long idProduct,
            @RequestBody ProductDTO dto,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Email") String userEmail,
            @RequestHeader("X-User-Type") Integer userType
    ) throws Exception{

        if(userType != 0 ) {
            throw new AuthenticationException("Usuário sem premissão");
        }

        var product = convertDto2Entity(dto);
        product.setId(idProduct);
        product.setStock(10);
        repository.save(product);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{idProduct}")
    public ResponseEntity<String> delete(
            @PathVariable Long idProduct,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Email") String userEmail,
            @RequestHeader("X-User-Type") Integer userType
    ) throws Exception{

        if(userType != 0 ) {
            throw new AuthenticationException("Usuário sem premissão");
        }

        repository.deleteById(idProduct);

        return ResponseEntity.ok("Excluído");
    }


    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuth(AuthenticationException e){
        String message = e.getMessage().replaceAll("[\\r\\n]", "");
        return ResponseEntity.status(403).body(message);
    }
}
