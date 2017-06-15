package com.catfish.entity;

/**
 * Created by A on 2017/4/27.
 */
public class UserCard {

    private String cardNo;
    private Long userId;

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserCard{" +
                "cardNo='" + cardNo + '\'' +
                ", userId=" + userId +
                '}';
    }
}
