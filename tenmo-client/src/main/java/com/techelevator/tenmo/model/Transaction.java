package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transaction {
    private Account sender= new Account();
    private Account receiver = new Account();
    private Transfer transfer = new Transfer();

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

    public Transfer getTransfer() {
        return transfer;
    }


    //method
    public void sendMoney(BigDecimal amount) {
        if (sender.getBalance().compareTo(amount) > 0) {
            sender.setBalance(sender.getBalance().subtract(amount));
            receiver.setBalance(receiver.getBalance().add(amount));

            transfer.setAmount(amount);
            transfer.setTransferTypeId(2);
            transfer.setTransferStatusId(2);
            transfer.setAccountFrom(sender.getAccountId());
            transfer.setAccountTo(receiver.getAccountId());
        } else {
            System.out.println("Not enough money in the account!");
        }
    }

    public void setTransferAccountFrom() {
       transfer.setAccountFrom(sender.getUserId());
    }

    public void setTransferAccountTo(){

         transfer.setAccountTo(receiver.getUserId());
    }


}
