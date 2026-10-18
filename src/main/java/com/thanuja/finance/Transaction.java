package com.thanuja.finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    public enum Type { INCOME, EXPENSE }

    private long id;
    private LocalDateTime timestamp;
    private Type type;
    private String category;
    private BigDecimal amount;
    private String remarks;

    public Transaction() {}

    public Transaction(long id, LocalDateTime timestamp, Type type,
                       String category, BigDecimal amount, String remarks) {
        this.id = id;
        this.timestamp = timestamp;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.remarks = remarks == null ? "" : remarks;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
