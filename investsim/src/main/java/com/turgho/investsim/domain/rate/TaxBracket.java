package com.turgho.investsim.domain.rate;

import java.math.BigDecimal;

import com.turgho.investsim.domain.exception.InvalidDaysException;

public enum TaxBracket {
    UP_TO_180_DAYS(180, new BigDecimal("0.225")),
    UP_TO_360_DAYS(360, new BigDecimal("0.20")),
    UP_TO_720_DAYS(720, new BigDecimal("0.175")),
    ABOVE_720_DAYS(-1, new BigDecimal("0.15"));

    private final int maxDays;
    private final BigDecimal rate;

    TaxBracket(int maxDays, BigDecimal rate) {
        this.maxDays = maxDays;
        this.rate = rate;
    }

    public static TaxBracket findByDays(int days) {
        if (days < 0) {
            throw new InvalidDaysException(days);
        }
        for (TaxBracket bracket : values()) {
            if (bracket.maxDays == -1 || days <= bracket.maxDays) {
                return bracket;
            }
        }
        return ABOVE_720_DAYS;
    }

    public BigDecimal rate() { return rate; }
    public int maxDays() { return maxDays; }
}
