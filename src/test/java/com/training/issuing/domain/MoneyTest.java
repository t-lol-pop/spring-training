package com.training.issuing.domain;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    @DisplayName("2 + 3 は 5 になる")
    void normalAddition() {
        Money testMoney = new Money(2);
        Money plusMoney = new Money(3);
        Money resultMoney = testMoney.plus(plusMoney);
        assertEquals(5, resultMoney.getAmount());
    }

    @Test
    @DisplayName("2 - 1 は 1 になる")
    void normalSubtraction() {
        Money testMoney = new Money(2);
        Money minusMoney = new Money(1);
        Money resultMoney = testMoney.minus(minusMoney);
        assertEquals(1, resultMoney.getAmount());
    }

    @Test
    @DisplayName("Moneyは不変")
    void immutableMoney() {
        Money testMoney = new Money(2);
        Money minusMoney = new Money(1);
        Money resultMoney = testMoney.minus(minusMoney);
        assertEquals(2, testMoney.getAmount());
        assertNotSame(resultMoney, testMoney);
    }

    @Test
    @DisplayName("2 - 2 は 0 になる")
    void subtractionToZero() {
        Money testMoney = new Money(2);
        Money minusMoney = new Money(2);
        Money resultMoney = testMoney.minus(minusMoney);
        assertEquals(0, resultMoney.getAmount());
    }

    @Test
    @DisplayName("減算結果がマイナスとなった場合に例外が発生することの確認")
    void throwsWhenInvalid() {
        Money testMoney = new Money(2);
        Money minusMoney = new Money(3);
        assertThrows(InsufficientBalanceException.class, () -> {
            testMoney.minus(minusMoney);
        });
    }

    @Test
    @DisplayName("金額が同じMoney同士はequalsでtrueになる")
    void equalAmountsAreEqual() {
        Money a = new Money(100);
        Money b = new Money(100);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("金額が異なるMoney同士はequalsでfalseになる")
    void differentAmountsAreNotEqual() {
        Money a = new Money(100);
        Money b = new Money(200);
        assertNotEquals(a, b);
    }
}