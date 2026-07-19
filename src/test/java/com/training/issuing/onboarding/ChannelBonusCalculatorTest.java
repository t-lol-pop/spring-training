package com.training.issuing.onboarding;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

class ChannelBonusCalculatorTest {

    private final ChannelBonusCalculator calculator = new ChannelBonusCalculator(
            List.of(new WebChannelBonusPolicy(), new AppChannelBonusPolicy(), new StoreChannelBonusPolicy()));

    @Test
    @DisplayName("WEBチャネルは100ポイント")
    void webChannelEarns100Points() {
        assertThat(calculator.calculate(Channel.WEB)).isEqualTo(100);
    }

    @Test
    @DisplayName("APPチャネルは200ポイント")
    void appChannelEarns200Points() {
        assertThat(calculator.calculate(Channel.APP)).isEqualTo(200);
    }

    @Test
    @DisplayName("STOREチャネルは50ポイント")
    void storeChannelEarns50Points() {
        assertThat(calculator.calculate(Channel.STORE)).isEqualTo(50);
    }
}
