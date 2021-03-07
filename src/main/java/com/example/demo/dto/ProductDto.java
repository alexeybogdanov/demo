package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class ProductDto {

    private String name;

    @JsonProperty("contain_articles")
    private List<ProductArticlesDto> productArticlesDtoList;
}
