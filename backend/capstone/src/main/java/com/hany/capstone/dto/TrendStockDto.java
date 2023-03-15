package com.hany.capstone.dto;
import lombok.Data;

import java.sql.Date;


@Data
public class TrendStockDto {
    private String ticker;
    private String company_name;
    private int period;
    private int label;
    private int rank;
}
