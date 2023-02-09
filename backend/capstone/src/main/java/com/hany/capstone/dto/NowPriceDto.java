package com.hany.capstone.dto;

import lombok.Data;

@Data
public class NowPriceDto {
    private String ticker;
    private int start_price;
    private int high_price;
    private int low_price;
    private int end_price;
    private float rate;
}
