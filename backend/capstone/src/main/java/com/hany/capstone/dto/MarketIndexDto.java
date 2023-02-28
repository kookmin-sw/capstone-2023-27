package com.hany.capstone.dto;

import lombok.Data;

@Data
public class MarketIndexDto {
    private String market;
    private float now_value;
    private float change_value;
    private float change_rate;
}
