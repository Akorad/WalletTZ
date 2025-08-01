package org.Akorad.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type")
    private OperationType operationType;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "comment")
    private String comment;


    public static Transaction deposit(Wallet wallet, BigDecimal amount, String comment) {
        return new Transaction() {{
            setWallet(wallet);
            setOperationType(OperationType.DEPOSIT);
            setAmount(amount);
            setTransactionDate(LocalDateTime.now());
            setComment(comment);
        }};
    }

    public static Transaction withdraw(Wallet wallet, BigDecimal amount, String comment) {
        return new Transaction() {{
            setWallet(wallet);
            setOperationType(OperationType.WITHDRAWAL);
            setAmount(amount);
            setTransactionDate(LocalDateTime.now());
            setComment(comment);
        }};
    }

    public static Transaction transfer(Wallet fromWallet, Wallet toWallet, BigDecimal amount, String comment) {
        return new Transaction() {{
            setWallet(fromWallet);
            setOperationType(OperationType.TRANSFER);
            setAmount(amount);
            setTransactionDate(LocalDateTime.now());
            setComment(comment);
        }};
    }
}
