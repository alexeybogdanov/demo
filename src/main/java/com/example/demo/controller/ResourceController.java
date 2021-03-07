package com.example.demo.controller;

import com.example.demo.dto.InventoryRequestDto;
import com.example.demo.dto.ProductRequestDto;
import com.example.demo.dto.ProductResponseDto;
import com.example.demo.service.ArticleService;
import com.example.demo.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ResourceController {

    private final ProductService productService;

    private final ArticleService articleService;

    public ResourceController(ProductService productService, ArticleService articleService) {
        this.productService = productService;
        this.articleService = articleService;
    }

    @PostMapping(value = "/products", consumes = "application/json")
    public ResponseEntity<Void> add(@RequestBody ProductRequestDto productRequestDto) {
        productService.addProducts(productRequestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/products/sell/{id}", consumes = "application/json")
    public ResponseEntity<Void> sell(@PathVariable long id) {
        productService.sell(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/products/list")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponseDto> list() {
        return productService.listAvailableProducts();
    }

    @PostMapping(value = "/inventory", consumes = "application/json")
    public ResponseEntity<?> addArticles(@RequestBody InventoryRequestDto inventoryRequestDto) {
        articleService.addArticles(inventoryRequestDto);
        return ResponseEntity.ok().build();
    }
}
