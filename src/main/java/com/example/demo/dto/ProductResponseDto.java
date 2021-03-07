package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ProductResponseDto {


    private String name;

    private Long  quantity;

    @JsonProperty("contain_articles")
    private List<ProductArticlesDto> productArticlesDtoList;
}
