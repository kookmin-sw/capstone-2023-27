package com.hany.capstone.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class DatePriceDto {


    private String ticker;
    private int start_price;
    private int high_price;
    private int low_price;
    private int end_price;
    private int share_volume;
    private int trade_volume;
    private Date date;
    private float rate;
    private String company_name;

}