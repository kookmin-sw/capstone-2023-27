package com.hany.capstone.dto;

import lombok.Data;


@Data
public class PeriodBestLabelDto {
    private int period;
    private int label;
    private float rate_mean;
    private int count;
    private int ranking;
}
