package com.training.issuing.member;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(String memberId) {
        super("指定された会員が見つかりません: " + memberId);
    }
}
