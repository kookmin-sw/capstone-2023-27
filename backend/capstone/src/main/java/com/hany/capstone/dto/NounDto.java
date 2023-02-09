package com.hany.capstone.dto;

import lombok.Data;

@Data
public class NounDto {
    private String ticker;
    private String noun;
    private int count;
    private String company_name;
    private int end_price;
    private float rate;
}
