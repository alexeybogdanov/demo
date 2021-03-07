package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ProductArticlesDto {

    @JsonProperty("art_id")
    private long artId;

    @JsonProperty("amount_of")
    private int amount;
}
