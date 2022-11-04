package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferInfo;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Component
public class JdbcUserDao implements UserDao {

    private static final BigDecimal STARTING_BALANCE = new BigDecimal("1000.00");
    private final JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int findIdByUsername(String username) {
        if (username == null) throw new IllegalArgumentException("Username cannot be null");

        int userId;
        try {
            userId = jdbcTemplate.queryForObject("SELECT user_id FROM tenmo_user WHERE username = ?", int.class, username);
        } catch (NullPointerException | EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("User " + username + " was not found.");
        }

        return userId;
    }

    @Override
    public User getUserById(int userId) {
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        if (results.next()) {
            return mapRowToUser(results);
        } else {
            return null;
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            User user = mapRowToUser(results);
            users.add(user);
        }

        return users;
    }

    @Override
    public User findByUsername(String username) {
        if (username == null) throw new IllegalArgumentException("Username cannot be null");

        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE username = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next()) {
            return mapRowToUser(rowSet);
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    @Override
    public boolean create(String username, String password) {

        // create user
        String sql = "INSERT INTO tenmo_user (username, password_hash) VALUES (?, ?) RETURNING user_id";
        String password_hash = new BCryptPasswordEncoder().encode(password);
        Integer newUserId;
        newUserId = jdbcTemplate.queryForObject(sql, Integer.class, username, password_hash);

        if (newUserId == null) return false;

        // create account
        sql = "INSERT INTO account (user_id, balance) values(?, ?)";
        try {
            jdbcTemplate.update(sql, newUserId, STARTING_BALANCE);
        } catch (DataAccessException e) {
            return false;
        }

        return true;
    }

    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("USER");
        return user;
    }




    @Override
   public Account getAccountById(int id) {
    BigDecimal balance = null;
    Account account = null;
    String sql = "select account_id, user_id, balance from account where user_id = ?;";
    SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
    if(results.next()) {
        account = mapRowToAccount(results);
    }
    return account;
    }

    @Override
    public BigDecimal updateBalance(BigDecimal newBalance, int userId) {
        String sql = "update account " +
                "set balance = ? " +
                "where user_id = ? " +
                "returning balance;";
       BigDecimal updatedBalance = jdbcTemplate.queryForObject(sql, BigDecimal.class, newBalance, userId);

        return updatedBalance;
    }

    @Override
    public Account getAccountByUsername(String username){
        Account account = null;
        String sql = "select account_id, account.user_id, balance from tenmo_user " +
                "join account on tenmo_user.user_id = account.user_id " +
                "where username = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
        if(results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }

    @Override
    public List<TransferInfo> getTransferInfoByUsername(String username) {
        List<TransferInfo> transferInfoList = new ArrayList<>();
        String sql = "select username, amount from transfer " +
                "join account on account.account_id = transfer.account_to " +
                "join tenmo_user on tenmo_user.user_id = account.user_id " +
                "where username = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
        while (results.next()) {
            transferInfoList.add(mapRowToTransferInfo(results));
        }
        return transferInfoList;
    }

    @Override
    public List<TransferInfo> getTransferInfo(int currentUserAccountId) {
        List<TransferInfo> transferInfoList = new ArrayList<>();
        String sql = "select username, amount from transfer " +
                "left join account on account.account_id = transfer.account_to " +
                "join tenmo_user on tenmo_user.user_id = account.user_id " +
                "where account_from = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, currentUserAccountId);
        while (results.next()) {
            transferInfoList.add(mapRowToTransferInfo(results));
        }
        return transferInfoList;
    }

    @Override
    public List<User> getAllUsers(){
        List<User> users = new ArrayList<>();
        String sql = "select username from tenmo_user;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()){
            users.add(mapRowToUsername(results));
        }
        return users;
    }
//    @Override
//    public Transfer createTransfer(Transfer transfer){
//        Transfer newTransfer = null;
//        String sql = "insert into transfer (transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
//                "values (?,?,?,?,?,?) " +
//                "returning transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount;";
//        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, Transfer.class, transfer.getTransferId(), transfer.getTransferTypeId(),
//                transfer.getTransferStatusId(), transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
//        if(results.next()) {
//            newTransfer =mapRowToTransfer(results);
//        }
//        return newTransfer;
//
//    }

    @Override
    public void createTransfer(Transfer transfer) {
        String sql = "insert into transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "values (?,?,?,?,?) " +
                "returning transfer_id;";
        Integer transferId = jdbcTemplate.queryForObject(sql, Integer.class, transfer.getTransferTypeId(),
                transfer.getTransferStatusId(), transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        System.out.println(transferId);

    }




    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setTransferTypeId(rs.getInt("transfer_type_id"));
        transfer.setTransferStatusId(rs.getInt("transfer_status_id"));
        transfer.setAccountFrom(rs.getInt("account_from"));
        transfer.setAccountTo(rs.getInt("account_to"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        return transfer;
    }


    private TransferInfo mapRowToTransferInfo(SqlRowSet rs) {
        TransferInfo transferInfo = new TransferInfo();
        transferInfo.setUsername(rs.getString("username"));
        transferInfo.setAmount(rs.getBigDecimal("amount"));
        return transferInfo;
    }

    private Account mapRowToAccount(SqlRowSet rs) {

        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }

    private User mapRowToUsername(SqlRowSet rs) {
        User user = new User();
        user.setUsername(rs.getString("username"));
        user.setId(0);
        user.setPassword("hh");
        user.setActivated(true);
        user.setAuthorities("USER");
        return user;
    }




}
