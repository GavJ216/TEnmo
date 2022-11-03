package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.util.List;

public class JdbcUserDaoTests extends BaseDaoTests {
    protected static final User USER_1 = new User(1001, "user1", "user1", "USER");
    protected static final User USER_2 = new User(1002, "user2", "user2", "USER");
    private static final User USER_3 = new User(1003, "user3", "user3", "USER");
    protected static final User USER_4 = new User(1004,"user4","user4","USER");

    private JdbcUserDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcUserDao(jdbcTemplate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findIdByUsername_given_null_throws_exception() {

      sut.findIdByUsername(null);

    }



    @Test(expected = UsernameNotFoundException.class)
    public void findIdByUsername_given_invalid_username_throws_exception() {
        sut.findIdByUsername("invalid");
    }



    @Test
    public void findIdByUsername_given_valid_user_returns_user_id() {

        int actualUserId = sut.findIdByUsername(USER_1.getUsername());

        Assert.assertEquals(USER_1.getId(), actualUserId);
    }


    @Test(expected = IllegalArgumentException.class)
    public void findByUsername_given_null_throws_exception() {
        sut.findByUsername(null);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void findByUsername_given_invalid_username_throws_exception() {
        sut.findByUsername("invalid");
    }


    @Test
    public void findByUsername_given_valid_user_returns_user() {
        User actualUser = sut.findByUsername(USER_1.getUsername());

        Assert.assertEquals(USER_1, actualUser);
    }


    @Test
    public void getUserById_given_invalid_user_id_returns_null() {
        User actualUser = sut.getUserById(-1);

        Assert.assertNull(actualUser);
    }


    @Test
    public void getUserById_given_valid_user_id_returns_user() {
        User actualUser = sut.getUserById(USER_1.getId());

        Assert.assertEquals(USER_1, actualUser);
    }


    @Test
    public void findAll_returns_all_users() {
        List<User> users = sut.findAll();

        Assert.assertNotNull(users);
        Assert.assertEquals(3, users.size());
        Assert.assertEquals(USER_1, users.get(0));
        Assert.assertEquals(USER_2, users.get(1));
        Assert.assertEquals(USER_3, users.get(2));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void create_user_with_null_username() {
        sut.create(null, USER_3.getPassword());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void create_user_with_existing_username() {
        sut.create(USER_1.getUsername(), USER_3.getPassword());
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_user_with_null_password() {
        sut.create(USER_3.getUsername(), null);
    }

    @Test
    public void create_user_creates_a_user() {
        User newUser = new User(-1, "new", "user", "USER");

        boolean userWasCreated = sut.create(newUser.getUsername(), newUser.getPassword());

        Assert.assertTrue(userWasCreated);

        User actualUser = sut.findByUsername(newUser.getUsername());
        newUser.setId(actualUser.getId());

        actualUser.setPassword(newUser.getPassword()); // reset password back to unhashed password for testing
        Assert.assertEquals(newUser, actualUser);
    }

// BELOW THIS LINE ARE TESTS WRITTEN BY RLUTSOCK
    // account is auto generated when users are created.

    @Test // this method actually looks up by userID not accountID
    public void get_account_by_id_returns_proper_account() {

        // must create a user to auto generate an account to test from.
        boolean createAccount = sut.create("aa","aa"); // will create a new account, should have the first generated id of 1004

        boolean testBool = false;                                       // init testBool as false until condition is met.

        Account actualAccount = sut.getAccountById(1004);               // run the getAccountById method against the expected userId of 1004.

        if(actualAccount != null){                                      // if the database successfully finds an account and returns an object ( not null )
        testBool = true;                                                // the account was found, so return true.
        }

        Assert.assertTrue(testBool);

    }

    @Test // this method actually looks up by userID not accountID
    public void get_account_by_id_with_bad_id_returns_false() {

        // must create a user to auto generate an account to test from.
        boolean createAccount = sut.create("aa","aa"); // will create a new account, should have the first generated id of 1004

        boolean testBool = false;                                       // init testBool as false until condition is met.

        Account actualAccount = sut.getAccountById(1005);               // run the getAccountById method against the bad data of 1005, will return false.

        if(actualAccount != null){                                      // if the database successfully finds an account and returns an object ( not null )
            testBool = true;                                                // the account was found, so return true.
        }

        Assert.assertFalse(testBool);

    }

    @Test
    public void update_balance_accurately_updates_balance() {

        BigDecimal newBalance = BigDecimal.valueOf(1100);               // new BigDecimal variable holding an amount to expect.

        boolean createAccount = sut.create("zz","zz");  //generate a new user, defaults to userID:1004, balance:1000
        boolean successfulUpdate = false;                               // init actual boolean.

//        Account testAccount = sut.getAccountById(1008);
        BigDecimal balanceTest = sut.getAccountById(1004).getBalance();

       BigDecimal outcome = sut.updateBalance(newBalance,1002);


        //int integerBalance = sut.getAccountById(1004).getBalance();
//        BigDecimal.valueOf(sut.getAccountById(1004).getBalance());

        if(testAccount.getBalance().compareTo(newBalance)==0){    // if the balance of the searched-for account is equal to the expected value
            successfulUpdate = true;                                //assign actual boolean to true.
        }

        Assert.assertTrue(successfulUpdate) ;

    }
//
//    @Test
//    public void name () {
//
//    }




}
