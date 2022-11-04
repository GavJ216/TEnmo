package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferInfo;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserDao {

    List<User> findAll();

    User getUserById(int id);

    User findByUsername(String username);

    int findIdByUsername(String username);

    boolean create(String username, String password);

    Account getAccountById(int id);

    Account getAccountByUsername(String username);

     BigDecimal updateBalance(BigDecimal newBalance, int userId);

     void createTransfer(Transfer transfer);

    public List<TransferInfo> getTransferInfoByUsername(String username);

    public List<TransferInfo> getTransferInfo(int currentUserAccountId);

    public List<User> getAllUsers();

    public Transfer getTransferByTransferId(int transferId);

    public User getUserNameByAccountId(int accountId);

    public boolean updateTransferTypeAndStatus(int newTransferStatusId, int transferId);
}
