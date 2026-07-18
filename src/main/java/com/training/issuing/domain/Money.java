package com.training.issuing.domain;

import java.util.Objects;

public class Money {
    private final long amount;
    private final String currency = "JPY";

    public Money(long amount) {
        this.amount = amount;
    }

    public Money plus(Money other) {
        return new Money(this.amount + other.amount);
    }

    public Money minus(Money other) {
        long resultAmount = this.amount - other.amount;
        if (resultAmount < 0) {
            throw new InsufficientBalanceException(
                    "残高不足です: " + this.amount + "円から" + other.amount + "円を減算できません");
        }
        return new Money(resultAmount);
    }

    public long getAmount() {
        return this.amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Money)) {
            return false;
        }
        Money other = (Money) o;
        return this.amount == other.amount && this.currency.equals(other.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.amount, this.currency);
    }
}
