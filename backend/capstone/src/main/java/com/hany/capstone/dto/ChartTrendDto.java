package com.hany.capstone.dto;
import lombok.Data;

import java.sql.Date;


@Data
public class ChartTrendDto {
    private int end_price;
    private Date date;
    private int period;
    private int label;
}
