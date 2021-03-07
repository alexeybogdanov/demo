package com.example.demo.service;

import com.example.demo.dto.ArticleDto;
import com.example.demo.dto.InventoryRequestDto;
import com.example.demo.entity.Article;
import com.example.demo.repository.ArticleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Transactional
    public void addArticles(InventoryRequestDto inventoryRequestDto) {
        inventoryRequestDto.getArticleDtoList().forEach(this::save);
    }

    private void save(ArticleDto articleDto) {
        Optional<Article> optionalArticle = articleRepository.findById(articleDto.getId());
        Article article;
        if (optionalArticle.isPresent()) {
            article = optionalArticle.get();
            article.setName(articleDto.getName());
            article.setStock(article.getStock() + articleDto.getStock());
        } else {
            article = Article.builder()
                    .id(articleDto.getId())
                    .name(articleDto.getName())
                    .stock(articleDto.getStock()).build();
        }
        articleRepository.save(article);
    }
}