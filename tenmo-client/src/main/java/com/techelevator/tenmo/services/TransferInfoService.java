package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.TransferInfo;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class TransferInfoService {
    public static final String API_BASE_URL = "http://localhost:8080/api/transferInfos/";
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public TransferInfo[] getTransferInfoByUsername(String username) {
        TransferInfo[]  transferInfos = null;
        try {
            ResponseEntity<TransferInfo[]> response =
                    restTemplate.exchange(API_BASE_URL +  username, HttpMethod.GET,
                            makeAuthEntity(), TransferInfo[].class);
            transferInfos = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transferInfos;
    }

    public TransferInfo[] getTransferInfo(int currentUserAccountId) {
        TransferInfo[]  transferInfos = null;
        try {
            ResponseEntity<TransferInfo[]> response =
                    restTemplate.exchange(API_BASE_URL + "/1/" + currentUserAccountId, HttpMethod.GET,
                            makeAuthEntity(), TransferInfo[].class);
            transferInfos = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transferInfos;
    }

    public User[] getAllUsers() {
        User[]  users = null;
        try {
            ResponseEntity<User[]> response =
                    restTemplate.exchange("http://localhost:8080/api/users/", HttpMethod.GET,
                            makeAuthEntity(), User[].class);
            users = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return users;
    }



    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
