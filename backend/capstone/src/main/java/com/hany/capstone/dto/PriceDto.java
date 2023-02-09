package com.hany.capstone.dto;

import lombok.Data;

@Data
public class PriceDto {
    private String ticker;
    private int end_price;
    private float rate;
    private String company_name;

}
