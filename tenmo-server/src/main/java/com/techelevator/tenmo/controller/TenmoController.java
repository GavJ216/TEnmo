package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferInfo;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
//@PreAuthorize("isAuthenticated()")
public class TenmoController {
    private UserDao userDao;


    public TenmoController(UserDao userDao) {
        this.userDao = userDao;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public boolean registerUser(@Valid @RequestBody UserDao userDao, String username, String password) {
        return userDao.create(username, password);
    }


    @RequestMapping(path = "/accounts/{id}", method = RequestMethod.GET)
    public Account getAccountInfo(@PathVariable int id) {
        Account account = userDao.getAccountById(id);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Not Found");
        } else {
            return account;
        }
    }

    @PreAuthorize("permitAll")
    @RequestMapping(path = "/accounts/username/{username}", method = RequestMethod.GET)
    public Account accountByUsername(@PathVariable String username) {



        return userDao.getAccountByUsername(username);
    }

    @RequestMapping(path = "/accounts/{id}", method = RequestMethod.PUT)
    public BigDecimal update(@Valid @RequestBody Account account, @PathVariable int id) {
        BigDecimal newBalance = userDao.updateBalance(account.getBalance(), id);
        return newBalance;
    }

    @RequestMapping(path = "/transfers", method = RequestMethod.POST)
    public void createTransfer(@Valid @RequestBody Transfer transfer) {
        userDao.createTransfer(transfer);
    }

    @RequestMapping(path = "/transferInfos/{username}", method = RequestMethod.GET)
    public List<TransferInfo> getTransferInfosByUsername(@PathVariable String username) {
        return userDao.getTransferInfoByUsername(username);
    }

    @RequestMapping(path = "/transferInfos/1/{currentUserAccountId}", method = RequestMethod.GET)
    public List<TransferInfo> getTransferInfos(@PathVariable int currentUserAccountId) {
        return userDao.getTransferInfo(currentUserAccountId);
    }

    @RequestMapping(path = "/users/", method = RequestMethod.GET)
    public List<User> getUsers() {
        return userDao.getAllUsers();
    }


    @RequestMapping(path = "/transfers/{transferId}", method = RequestMethod.GET)
    public Transfer getTransfer(@PathVariable int transferId) {
        return userDao.getTransferByTransferId(transferId);
    }

    @RequestMapping(path = "/users/{accountId}", method = RequestMethod.GET)
    public User getUserByUsernameId(@PathVariable int accountId) {
        return userDao.getUserNameByAccountId(accountId);
    }
}
