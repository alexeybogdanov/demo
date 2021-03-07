package com.example.demo.service;

import com.example.demo.dto.ProductArticlesDto;
import com.example.demo.dto.ProductDto;
import com.example.demo.dto.ProductRequestDto;
import com.example.demo.dto.ProductResponseDto;
import com.example.demo.entity.Article;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductArticle;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.ProductArticlesRepository;
import com.example.demo.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductArticlesRepository productArticlesRepository;
    private final ArticleRepository articleRepository;

    public ProductService(ProductRepository productRepository, ProductArticlesRepository productArticlesRepository,
                          ArticleRepository articleRepository) {
        this.productRepository = productRepository;
        this.productArticlesRepository = productArticlesRepository;
        this.articleRepository = articleRepository;
    }

    public List<ProductResponseDto> listAvailableProducts() {
        List<ProductResponseDto> productResponseDtoList = new ArrayList<>();
        Iterable<Product> products = productRepository.findAll();
        for (Product product : products) {
            long quantity = checkProductAvailabilityWithCurrentInventory(product);
            if (quantity > 0) {
                ProductResponseDto productResponseDto = toProductResponseDto(product, quantity);
                productResponseDtoList.add(productResponseDto);
            }
        }
        return productResponseDtoList;
    }

    private long checkProductAvailabilityWithCurrentInventory(Product product) {
        long maxAvailableProductCombination = 0;
        for (ProductArticle productArticle : product.getProductArticlesList()) {
            long stock = 0;
            if (productArticle.getArticle() != null) {
                stock = productArticle.getArticle().getStock();
            } else {
                log.error("Product inventory relation for product {} with id: {} is not exist.", product.getName(), product.getId());
            }
            int amount = productArticle.getAmount();
            if (stock < amount) {
                return 0L;
            } else {
                maxAvailableProductCombination = stock / amount;
            }
        }
        return maxAvailableProductCombination;
    }

    private ProductResponseDto toProductResponseDto(Product product, long quantity) {
        return ProductResponseDto.builder()
                .name(product.getName())
                .quantity(quantity)
                .productArticlesDtoList(product.getProductArticlesList().stream()
                        .map(productArticle -> ProductArticlesDto.builder()
                                .amount(productArticle.getAmount())
                                .artId(productArticle.getArticle().getId()).build()).collect(Collectors.toList())).build();
    }

    @Transactional
    public void sell(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException(String.format("Product with id: %d not found.", id)));
        decreaseInventory(product);
    }

    private void decreaseInventory(Product product) {
        product.getProductArticlesList().forEach(productArticle -> {
            long stock = productArticle.getArticle().getStock();
            int amount = productArticle.getAmount();
            if (stock - amount >= 0) {
                productArticle.getArticle().setStock(stock - amount);
            } else
                throw new RuntimeException(String.format("Not enough articles: %s with id: %d.", productArticle.getArticle().getName(),
                        productArticle.getArticle().getId()));
        });
    }

    @Transactional
    public void addProducts(ProductRequestDto productRequestDto) {
        productRequestDto.getProducts().forEach(this::save);
    }

    private void save(ProductDto productDto) {
        Product product = Product.builder()
                .name(productDto.getName()).build();
        productRepository.save(product);
        extractAndSaveProductArticles(productDto, product);
    }

    private void extractAndSaveProductArticles(ProductDto productDto, Product product) {
        List<ProductArticlesDto> productArticlesDtoList = productDto.getProductArticlesDtoList();
        productArticlesDtoList.forEach(productArticlesDto -> {

            ProductArticle productArticle = ProductArticle.builder()
                    .product(product)
                    .amount(productArticlesDto.getAmount())
                    .article(findArticle(productArticlesDto.getArtId())).build();
            productArticlesRepository.save(productArticle);
        });
    }

    private Article findArticle(Long artId) {
        return articleRepository.findById(artId).orElse(null);
    }

}
