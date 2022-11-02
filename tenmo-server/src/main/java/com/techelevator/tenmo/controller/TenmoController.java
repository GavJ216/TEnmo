package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;

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

}
