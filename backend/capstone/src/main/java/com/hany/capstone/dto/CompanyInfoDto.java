package com.hany.capstone.dto;

import lombok.Data;

@Data
public class CompanyInfoDto {
    private String ticker;
    private String company_name;

    private String company_info;
    private int market_cap;
    private float per;
    private int eps;
    private float est_per;
    private int est_eps;
    private float pbr;
    private float  dvr;
}
