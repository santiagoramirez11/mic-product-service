package com.banreservas.product.integration.dto;

import lombok.Data;

@Data
public class ExchangeRateResponseDto {
    private String result;
    private String documentation;
    private String terms_of_use;
    private String time_last_update_utc;
    private String time_next_update_utc;
    private String base_code;
    private String target_code;
    private Double conversion_rate;
}