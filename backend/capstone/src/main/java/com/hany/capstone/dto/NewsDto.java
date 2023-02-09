package com.hany.capstone.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class NewsDto {
    private String ticker;
    private String provider;
    private Date date;
    private String rink;
    private String title;
}
