package com.turgho.investsim.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Mapeamento JPA — nao vaza pra fora de infrastructure/
@Entity
@Table(name = "simulations")
public class SimulationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "initial_value", nullable = false)
    private BigDecimal initialValue;

    @Column(nullable = false)
    private Integer months;

    @Column(name = "investment_type", nullable = false, length = 20)
    private String investmentType;

    @Column(name = "cdi_percentage")
    private BigDecimal cdiPercentage;

    @Column(name = "gross_amount", nullable = false)
    private BigDecimal grossAmount;

    @Column(name = "income_tax", nullable = false)
    private BigDecimal incomeTax;

    @Column(name = "net_amount", nullable = false)
    private BigDecimal netAmount;

    @Column(name = "net_return", nullable = false)
    private BigDecimal netReturn;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Construtor vazio obrigatorio pro Hibernate (instancia via reflection)
    protected SimulationEntity() {}

    public SimulationEntity(
            BigDecimal initialValue, int months, String investmentType,
            BigDecimal cdiPercentage, BigDecimal grossAmount, BigDecimal incomeTax,
            BigDecimal netAmount, BigDecimal netReturn) {
        this.initialValue = initialValue;
        this.months = months;
        this.investmentType = investmentType;
        this.cdiPercentage = cdiPercentage;
        this.grossAmount = grossAmount;
        this.incomeTax = incomeTax;
        this.netAmount = netAmount;
        this.netReturn = netReturn;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public BigDecimal getInitialValue() { return initialValue; }
    public int getMonths() { return months; }
    public String getInvestmentType() { return investmentType; }
    public BigDecimal getCdiPercentage() { return cdiPercentage; }
    public BigDecimal getGrossAmount() { return grossAmount; }
    public BigDecimal getIncomeTax() { return incomeTax; }
    public BigDecimal getNetAmount() { return netAmount; }
    public BigDecimal getNetReturn() { return netReturn; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
