package com.techelevator.tenmo.model;

import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

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

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transfer, headers);
    }
}
