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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductArticlesRepository productArticlesRepository;

    @Mock
    ArticleRepository articleRepository;

    public static Article article = Article.builder().id(1).name("leg").stock(12).build();
    public static Article articleNotInStock = Article.builder().id(1).name("leg").stock(3).build();

    public static ProductArticle productArticle = ProductArticle.builder().id(1).amount(4).product(Product.builder().id(1).build()).article(article).build();
    public static ProductArticle productArticleNotInStock = ProductArticle.builder().id(1).amount(4).product(Product.builder().id(2).build()).article(articleNotInStock).build();

    public static Product product = Product.builder().id(1).name("Chair").productArticlesList(Arrays.asList(productArticle)).build();
    public static Product productNotInStock = Product.builder().id(2).name("Chair").productArticlesList(Arrays.asList(productArticleNotInStock)).build();


    @Test
    void listAvailableProducts() {
        List<Product> products = Arrays.asList(product,productNotInStock);
        when(productRepository.findAll()).thenReturn(products);
        List<ProductResponseDto> productResponseDtoList = productService.listAvailableProducts();
        assertEquals(1, productResponseDtoList.size());
        assertEquals(3, productResponseDtoList.get(0).getQuantity());
    }

    @Test
    void sell() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        productService.sell(product.getId());
        verify(productRepository, times(1)).findById(any());
    }

    @Test
    void sellNotEnoughStock() {
        when(productRepository.findById(productNotInStock.getId())).thenReturn(Optional.of(productNotInStock));
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> productService.sell(productNotInStock.getId()));
        assertTrue(runtimeException.getMessage().contains("Not enough articles:"));

    }

    @Test
    void addProducts() {
        List<ProductArticlesDto> productArticlesDtoList = Arrays.asList(ProductArticlesDto.builder().amount(4).artId(1).build());
        List<ProductDto> productDtoList = Arrays.asList(ProductDto.builder().name("leg").productArticlesDtoList(productArticlesDtoList).build());
        ProductRequestDto productRequestDto = ProductRequestDto.builder().products(productDtoList).build();

        when(productRepository.save(any())).thenReturn(product);
        when(articleRepository.findById(any())).thenReturn(Optional.of(article));
        when(productArticlesRepository.save(any())).thenReturn(productArticle);

        productService.addProducts(productRequestDto);

        verify(productRepository, times(1)).save(any());
        verify(productArticlesRepository, times(1)).save(any());
    }
}