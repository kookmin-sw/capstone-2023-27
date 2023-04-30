package com.hany.capstone.dto;

import lombok.Data;

@Data
public class PriceDto {
    private String ticker;
    private int start_price;
    private int high_price;
    private int low_price;
    private int end_price;
    private int share_volume;
    private int trade_volume;
    private float rate;
    private String company_name;

}
