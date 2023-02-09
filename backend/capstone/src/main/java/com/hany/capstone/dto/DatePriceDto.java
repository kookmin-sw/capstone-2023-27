package com.hany.capstone.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class DatePriceDto {
    private String ticker;
    private int end_price;
    private Date date;
    private float rate;
    private String company_name;

}