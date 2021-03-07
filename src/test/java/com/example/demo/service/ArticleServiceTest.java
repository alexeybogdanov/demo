package com.example.demo.service;

import com.example.demo.dto.ArticleDto;
import com.example.demo.dto.InventoryRequestDto;
import com.example.demo.entity.Article;
import com.example.demo.repository.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks
    private ArticleService articleService;

    @Mock
    ArticleRepository articleRepository;

    @Captor
    ArgumentCaptor<Article> articleCaptor;

    public static Article articleInDB = Article.builder().id(1).name("leg").stock(12).build();
    public static ArticleDto articleDto = ArticleDto.builder().id(1).name("leg").stock(12).build();
    public static InventoryRequestDto inventoryRequestDto = InventoryRequestDto.builder().articleDtoList(Arrays.asList(articleDto)).build();

    @Test
    void addArticlesVerifyStockIsUpdated() {
        when(articleRepository.findById(any())).thenReturn(Optional.of(articleInDB));
        articleService.addArticles(inventoryRequestDto);

        verify(articleRepository, times(1)).save(articleInDB);
        assertEquals(24, articleInDB.getStock());
    }

    @Test
    void addNewArticles() {
        when(articleRepository.findById(any())).thenReturn(Optional.empty());
        articleService.addArticles(inventoryRequestDto);

        verify(articleRepository, times(1)).save(articleCaptor.capture());
        assertEquals(12, articleCaptor.getValue().getStock());
    }
}