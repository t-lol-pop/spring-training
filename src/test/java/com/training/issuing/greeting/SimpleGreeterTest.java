package com.training.issuing.greeting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class SimpleGreeterTest {

    @Test
    @DisplayName("会員名を渡すと、その名前を含む挨拶メッセージを返す")
    void greetReturnsMessageContainingMemberName() {
        Greeter greeter = new SimpleGreeter();

        String message = greeter.greet("山田太郎");

        assertEquals("山田太郎さん、ようこそ。", message);
    }
}
