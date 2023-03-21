package com.hany.capstone.dto;
import lombok.Data;

import java.sql.Date;


@Data
public class ChartTrendDto {
    private float end_price;
    private int period;
    private int label;
}
