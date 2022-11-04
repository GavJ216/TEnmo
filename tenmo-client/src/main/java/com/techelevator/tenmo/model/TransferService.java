package com.techelevator.tenmo.model;

import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class TransferService {
    public static final String API_BASE_URL = "http://localhost:8080/api/transfers/";
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Transfer createTransfer(Transfer transfer){
        HttpEntity<Transfer> entity = makeTransferEntity(transfer);
        Transfer returnedTransfer = null;
        try {
           returnedTransfer = restTemplate.postForObject(API_BASE_URL, entity, Transfer.class);

        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
            //TODO change catch body
        }

        return returnedTransfer;


    }

    public boolean updateTransferTypeAndStatus(Transfer transfer) {
        boolean updateSuccessful = false;
        HttpEntity<Transfer> entity = makeTransferEntity(transfer);
        try {
            restTemplate.put(API_BASE_URL, entity, Transfer.class);
            updateSuccessful = true;

        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
            //TODO change catch body
        }

        return updateSuccessful;
    }

//

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transfer, headers);
    }

    public Transfer getTransferByTransferId(int transferId){
        Transfer transfer = null;

        try{ResponseEntity<Transfer> response =
                restTemplate.exchange(API_BASE_URL + transferId,
                        HttpMethod.GET, makeAuthEntity(), Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage()); }
        return transfer;
    }

    public User getUsernameByAccountId(int accountId){
        User username = null;

        try{ResponseEntity<User> response =
                restTemplate.exchange("http://localhost:8080/api/users/" + accountId,
                        HttpMethod.GET, makeAuthEntity(), User.class);
            username = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage()); }
        return username;
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
