package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferInfoService;

import java.math.BigDecimal;
import java.util.Scanner;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private AccountService accountService = new AccountService();
    private TransferService transferService = new TransferService();
    private TransferInfoService transferInfoService = new TransferInfoService();


    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
        else {
            transferService.setAuthToken(currentUser.getToken());
            accountService.setAuthToken(currentUser.getToken());
            transferInfoService.setAuthToken(currentUser.getToken());
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
        System.out.println("Current Balance: "+ accountService.getBalance(currentUser.getUser().getId()));
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
        Scanner scanner = new Scanner(System.in);
        System.out.println("1) View all transactions or  2)View Transactions from a specific username? or  3)View Transaction by Transaction ID.");
        System.out.println("Select one:");
        String choice = scanner.nextLine();

        if(choice.equalsIgnoreCase("1")) {
            TransferInfo[] list = transferInfoService.getTransferInfo(accountService.getAccountMatchingUsername(currentUser.getUser().getUsername()).getAccountId());
            for(TransferInfo transferInfo: list){
                System.out.println("Username:  " + transferInfo.getUsername() + "     Amount you sent:  " + transferInfo.getAmount());
            }
        }
        else if(choice.equalsIgnoreCase("2")) {
            System.out.println("Enter username to view transactions: ");
            String usernameSearch = scanner.nextLine();
            Account account = accountService.getAccountMatchingUsername(usernameSearch);
            if(account != null) {
                if(usernameSearch.equalsIgnoreCase(currentUser.getUser().getUsername())) {
                    System.out.println("You will never send yourself money!");
                }
                else {
                   TransferInfo[] list = transferInfoService.getTransferInfoByUsername(usernameSearch);
                    for(TransferInfo transferInfo: list){
                        System.out.println("Username:  " + transferInfo.getUsername() + "     Amount you sent:  " + transferInfo.getAmount());
                    }
                }
            }
            else {
                System.out.println("Username not found!");
            }
        }
        else if(choice.equalsIgnoreCase("3")){
            System.out.println("Enter a transfer ID to view the transaction: ");
            String transferIdSearch = scanner.nextLine();
            int parsedString = Integer.parseInt(transferIdSearch);
            Transfer transfer = transferService.getTransferByTransferId(parsedString);

//            if(transfer.getAccountFrom() != currentUser.getUser().getId()){
//                System.out.println("You can only view your own transfers.");
//            }
            if(transfer != null){
                System.out.println("----------------------------");
                System.out.println("Transfer Details");
                System.out.println("----------------------------");

                System.out.println("Id: " + transfer.getTransferId());
                System.out.println("From: " + transferService.getUsernameByAccountId(transfer.getAccountFrom()).getUsername());
                System.out.println("To: " + transferService.getUsernameByAccountId(transfer.getAccountTo()).getUsername());

                if(transfer.getTransferStatusId() == 1){
                    System.out.println("Status: Pending");
                }
                if(transfer.getTransferStatusId() == 2){
                    System.out.println("Status: Approved");
                }
                if(transfer.getTransferStatusId() == 3){
                    System.out.println("Status: Rejected");
                }


                if(transfer.getTransferTypeId() == 1){
                    System.out.println("Type: Request");
                }

                if(transfer.getTransferTypeId() == 2){
                    System.out.println("Type: Send");
                }

                System.out.println("Amount: $" + transfer.getAmount());
            }
        }
        else {
            System.out.println("Not a valid response!");
        }
		
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
        System.out.println();
        System.out.println("THIS WAS OPTIONAL AND WE'RE NOT DOING IT!");
	}

	private void sendBucks() {
		// TODO Auto-generated method stub
		Scanner scanner = new Scanner(System.in);
        System.out.println("All usernames are:");
        User[] list = transferInfoService.getAllUsers();
        for(User user: list){
            System.out.println(user.getUsername());
        }
        System.out.println("Enter the username of the account you want to transfer to.");
        String userInput = scanner.nextLine();
        Account account = accountService.getAccountMatchingUsername(userInput);
        if(account != null) {
            if(userInput.equalsIgnoreCase(currentUser.getUser().getUsername())) {
                System.out.println("You cannot send yourself money!");
            }
            else {
                System.out.println("How much do you want to send?");
                String choice = scanner.nextLine();
                Account sender = accountService.getAccount(currentUser.getUser().getId());
                Transaction transaction = new Transaction(sender,account);
                BigDecimal bigDecimal = new BigDecimal(choice);
                transaction.sendMoney(bigDecimal);
                accountService.update(sender);
                accountService.update(account);
                transferService.createTransfer(transaction.getTransfer());
            }
        }
        else {
            System.out.println("Username not found!");
        }

	}

	private void requestBucks() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("All usernames are:");
        User[] list = transferInfoService.getAllUsers();
        for (User user : list) {
            System.out.println(user.getUsername());
        }
        System.out.println("Choose a user to request from: ");
        String userInput = scanner.nextLine();
        Account requestAccount = accountService.getAccountMatchingUsername(userInput);
//        Transfer requestTransfer = new Transfer();
        if (requestAccount != null) {
            if(userInput.equalsIgnoreCase(currentUser.getUser().getUsername())) {
                System.out.println("You cannot request money from yourself!");
            }
            else {
                System.out.println("How much do you want to request?");
                String choice = scanner.nextLine();
                Account sender = accountService.getAccount(currentUser.getUser().getId());
                Transaction transaction = new Transaction(sender,requestAccount);
                BigDecimal bigDecimal = new BigDecimal(choice);
                transaction.createTransferRequest(bigDecimal);

                transferService.createTransfer(transaction.getTransfer());
            }
        } else {
            System.out.println("Username not found!");
        }
    }


}
