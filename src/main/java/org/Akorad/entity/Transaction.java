package org.Akorad.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "transactions")
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
        Transaction tx = new Transaction();
        tx.setWallet(wallet);
        tx.setOperationType(OperationType.DEPOSIT);
        tx.setAmount(amount);
        tx.setTransactionDate(LocalDateTime.now());
        tx.setComment(comment);
        return tx;
    }

    public static Transaction withdraw(Wallet wallet, BigDecimal amount, String comment) {
        Transaction tx = new Transaction();
        tx.setWallet(wallet);
        tx.setOperationType(OperationType.WITHDRAWAL);
        tx.setAmount(amount);
        tx.setTransactionDate(LocalDateTime.now());
        tx.setComment(comment);
        return tx;
    }

    public static Transaction transfer(Wallet fromWallet, Wallet toWallet, BigDecimal amount, String comment) {
        Transaction tx = new Transaction();
        tx.setWallet(fromWallet);
        tx.setOperationType(OperationType.TRANSFER);
        tx.setAmount(amount);
        tx.setTransactionDate(LocalDateTime.now());
        tx.setComment(comment);
        return tx;
    }
}
