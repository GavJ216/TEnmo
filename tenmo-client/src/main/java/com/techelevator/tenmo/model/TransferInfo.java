package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class TransferInfo {

    private String username;
    private BigDecimal amount;


    public String getUsername(){
        return username;
    }

    public BigDecimal getAmount(){
        return amount;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
