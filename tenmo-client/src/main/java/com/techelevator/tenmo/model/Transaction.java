package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transaction {
    Account sender= new Account();
    Account receiver = new Account();

    public Transaction(Account sender, Account receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public Account getSender(){
        return sender;
    }

    public Account getReceiver() {
        return receiver;
    }


    //method
    public void sendMoney(BigDecimal amount) {
        if (sender.getBalance().compareTo(amount) > 0) {
            sender.setBalance(sender.getBalance().subtract(amount));
            receiver.setBalance(receiver.getBalance().add(amount));
        } else {
            System.out.println("Not enough money in the account!");
        }
    }


}
