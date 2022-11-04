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

//firstBigDecimal.compareTo(secondBigDecimal) >= 0 // ">="
    //method
    public void sendMoney(BigDecimal amount) {
        BigDecimal test = new BigDecimal(1);
        if(amount.compareTo(test)<0){
            System.out.println("Can't send a negative or 0 amount.");
        }
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

    public void createTransferRequest(BigDecimal requestAmount) {
        BigDecimal minRequest = new BigDecimal(1);
        if(requestAmount.compareTo(minRequest)<0){
            System.out.println("Your request cannot be less than $1");
        }
        transfer.setAmount(requestAmount);
        transfer.setTransferTypeId(1);
        transfer.setTransferStatusId(1);
        transfer.setAccountFrom(sender.getAccountId());
        transfer.setAccountTo(receiver.getAccountId());
    }

    public void setTransferAccountFrom() {
       transfer.setAccountFrom(sender.getUserId());
    }

    public void setTransferAccountTo(){

         transfer.setAccountTo(receiver.getUserId());
    }

    public void approveTransfer() {
        transfer.setTransferStatusId(2);
    }


}
