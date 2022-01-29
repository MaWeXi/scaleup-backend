package com.scaleup.backend.user.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class LeaderboardUserDTO {

    private String username;
    private BigDecimal portfolio_value;
    private BigDecimal freeBudget;

}
