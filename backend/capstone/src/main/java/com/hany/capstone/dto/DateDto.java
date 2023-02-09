package com.hany.capstone.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class DateDto {
    private Date day_minute1;
    private Date day_minute10;
    private Date day_date;
    private Date day_week;
}
