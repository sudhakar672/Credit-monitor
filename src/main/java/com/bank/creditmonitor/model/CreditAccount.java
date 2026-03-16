package com.bank.creditmonitor.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true, length = 20)
    private String accountNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Borrower borrower;

    @Column(name = "account_type", nullable = false, length = 40)
    private String accountType;

    @Column(name = "sanctioned_limit", nullable = false, precision = 15, scale = 2)
    private BigDecimal sanctionedLimit;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal outstanding;

    @Column(name = "utilization_pct", precision = 5, scale = 2, insertable = false, updatable = false)
    private BigDecimal utilizationPct;

    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "disbursement_date")
    private LocalDate disbursementDate;

    @Column(name = "maturity_date")
    private LocalDate maturityDate;

    @Column(length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column
    @Builder.Default
    private Integer dpd = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
