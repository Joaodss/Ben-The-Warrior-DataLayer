package com.ironhack.homework3.dao.main;

import com.ironhack.homework3.dao.classes.Account;
import com.ironhack.homework3.dao.classes.Contact;
import com.ironhack.homework3.dao.classes.Lead;
import com.ironhack.homework3.dao.classes.Opportunity;
import com.ironhack.homework3.enums.Industry;
import com.ironhack.homework3.enums.Product;
import com.ironhack.homework3.enums.Status;
import com.ironhack.homework3.utils.DatabaseUtility;
import com.ironhack.homework3.utils.Printer;
import com.ironhack.homework3.utils.PrinterMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

import static com.ironhack.homework3.utils.Utils.*;
import static com.ironhack.homework3.utils.Utils.validLocation;

@Component
public class Menu {
    private final Scanner scanner;
    @Autowired
    private DatabaseUtility db;
    //private final JsonDatabaseUtility db; TODO remove if not necessary

    // Variable to check if the user asked for the available commands
    private boolean showHelp;

    public Menu() {
        scanner = new Scanner(System.in);
        /*try {
            db.load();
        } catch (Exception e) {
            PrinterMenu.setWarning(e.getMessage());
        }*/
        setShowHelp(false);
    }

/*    private static final Menu menu = new Menu();

    public void printMenu() {
        menu.mainMenu();
    }*/
/*
    public void testLead() {
        var lead = new Lead(1, "Ula", "4346745", "sddf@dfgf.com", "San");
        leadRepository.save(lead);
        var allLeads = leadRepository.findAll();
        for (var c : allLeads) System.out.println("Our menu includes: " + c.getName());
    }*/


    public Menu(InputStream inputStream){
        scanner = new Scanner(inputStream);
        db  = new DatabaseUtility();
        setShowHelp(false);
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public void setShowHelp(boolean showHelp) {
        this.showHelp = showHelp;
    }

    // Core method of the application. This method is running while the app is running and only returns when closing the app
    public void mainMenu() {
        String input;
        boolean running = true;
        showHelp = false;
        while (running) {
            // if the user asked for available commands print help menu, otherwise print main menu
            if (isShowHelp()){
                PrinterMenu.printMenu("help");
                setShowHelp(false);
            } else {
                PrinterMenu.printMenu("main");
            }
            // get a user input, if it is valid compute the command otherwise print a warning message
            input = scanner.nextLine();
            if (validCommand(input)) {
                PrinterMenu.clearWarning();
                running = computeCommand(input);
            } else {
                PrinterMenu.setWarning("There is no such command as \"" + input + "\"! To see the list of available commands type help!");
            }
        }
    }

    // Method to compute commands after being validated
    public boolean computeCommand(String input) {
        String[] inputArray = input.trim().toLowerCase().split(" ");
        // commands are computed word by word and the appropriate method is called
        switch (inputArray[0]) {
            case "new":
                if (inputArray[1].equals("lead")){
                    promptLead();
                }
                break;
            case "show":
                switch (inputArray[1]) {
                    case "leads":
                    case "opportunities":
                    case "contacts":
                    case "accounts":
                        showMenu(inputArray[1]);
                        break;
                }
                break;
            case "lookup":
                switch (inputArray[1]) {
                    case "lead":
                        try {
                            // If there is no lead an error is thrown
                            // Otherwise the command can be correctly computed and the warning messages can be cleared
                            Lead lead = db.lookupLeadId(Integer.parseInt(inputArray[2]));
                            PrinterMenu.clearWarning();
                            PrinterMenu.lookupObject(lead);
                            promptDecision("enter");
                        } catch (IllegalArgumentException e) {
                            PrinterMenu.setWarning(e.getMessage());
                        }
                        break;
                    case "opportunity":
                        try {
                            // If there is no opportunity an error is thrown
                            // Otherwise the command can be correctly computed and the warning messages can be cleared
                            Opportunity opportunity = db.lookupOpportunityId(Integer.parseInt(inputArray[2]));
                            PrinterMenu.clearWarning();
                            PrinterMenu.lookupObject(opportunity);
                            boolean decision = promptDecision("enter back");
                            if (decision) {
                                PrinterMenu.lookupObject(opportunity, "contact");
                                promptDecision("enter");
                            }
                        } catch (IllegalArgumentException e) {
                            PrinterMenu.setWarning(e.getMessage());
                        }

                        break;
                    case "contact":
                        try {
                            // If there is no contact an error is thrown
                            // Otherwise the command can be correctly computed and the warning messages can be cleared
                            Contact contact = db.lookupContactId(Integer.parseInt(inputArray[2]));
                            PrinterMenu.clearWarning();
                            PrinterMenu.lookupObject(contact);
                            promptDecision("enter");
                        } catch (IllegalArgumentException e) {
                            PrinterMenu.setWarning(e.getMessage());
                        }
                        break;
                    case "account":
                        try {
                            // If there is no account an error is thrown
                            // Otherwise the command can be correctly computed and the warning messages can be cleared
                            Account account = db.lookupAccountId(Integer.parseInt(inputArray[2]));
                            PrinterMenu.clearWarning();
                            lookupAccountMenu(account);
                        } catch (IllegalArgumentException e) {
                            PrinterMenu.setWarning(e.getMessage());
                        }
                        break;
                    default:
                        break;
                }
                break;
            case "convert":
                promptConvert(Integer.parseInt(inputArray[1]));
                break;
            case "close-won":
                try {
                    // If there is no opportunity an error is thrown
                    // Otherwise the command can be correctly computed and the warning messages can be cleared
                    Opportunity opportunity = db.lookupOpportunityId(Integer.parseInt(inputArray[1]));
                    opportunity.setStatus(Status.CLOSED_WON);
                } catch (IllegalArgumentException e) {
                    PrinterMenu.setWarning(e.getMessage());
                }
                break;
            case "close-lost":
                try {
                    // If there is no opportunity an error is thrown
                    // Otherwise the command can be correctly computed and the warning messages can be cleared
                    Opportunity opportunity = db.lookupOpportunityId(Integer.parseInt(inputArray[1]));
                    opportunity.setStatus(Status.CLOSED_LOST);
                } catch (IllegalArgumentException e) {
                    PrinterMenu.setWarning(e.getMessage());
                }
                break;
            // show help menu with all available commands
            case "help":
                setShowHelp(true);
                break;
            /*// sava database into json file
            case "save":
                try{
                    //db.save();
                }catch (IOException e){
                    PrinterMenu.setWarning("An error as occurred. Database was not successfully saved!");
                }
                break; TODO delete save option */
            case "exit":
                PrinterMenu.printMenu("exit");
                /*if (promptDecision("exit")){
                    try{
                        db.save();
                    }catch (IOException e){
                        PrinterMenu.setWarning("An error as occurred. Database was not successfully saved!");
                    }
                }*/
                return false;
            default:
                break;
        }
        return true;
    }

    // Method to create the menu when looking up an account
    private void lookupAccountMenu(Account account){
        PrinterMenu.lookupObject(account);
        // Allow user to see list of contacts and opportunities n the looked up account
        while (true) {
            int answer = promptMultipleDecisions("contacts", "opportunities", "back");
            switch (answer) {
                case 0:
                    showContactsMenu(account.getContactList());
                    PrinterMenu.lookupObject(account);
                    break;
                case 1:
                    showOpportunitiesMenu(account.getOpportunityList());
                    PrinterMenu.lookupObject(account);
                    break;
                case 2:
                    return;
            }
        }
    }

    // Method to create the menu showing all available leads
    private void showMenu(String objectType) {
        int maxElements = PrinterMenu.getPrintMultipleObjectsMax();
        int currentIndex = 0;
        int currentPage = 0;
        int numPages;
        int decision;
        switch (objectType.toLowerCase()){
            case "leads":
                List<Lead> leadList = db.getAllLeads();
                if (leadList.size() > 0){
                    List<ArrayList<Lead>> listList = new ArrayList<>();
                    listList.add(new ArrayList<>());
                    for (Lead lead : leadList) {
                        if (currentIndex + Printer.numberOfTextRows(lead.toString()) < maxElements) {
                            currentIndex = currentIndex + Printer.numberOfTextRows(lead.toString());
                            listList.get(currentPage).add(lead);
                        } else {
                            listList.add(new ArrayList<>());
                            listList.get(++currentPage).add(lead);
                        }
                    }
                    currentPage = 0;
                    numPages = listList.size();
                    while (true) {
                        PrinterMenu.showLeads(listList.get(currentPage), currentPage == 0, currentPage + 1 == numPages);
                        if (listList.size() > 1) {
                            if (currentPage == 0) {
                                decision = promptMultipleDecisions("next", "back");
                                switch (decision) {
                                    case 0:
                                        currentPage++;
                                        break;
                                    case 1:
                                        return;
                                }
                            } else if (currentPage + 1 == numPages) {
                                decision = promptMultipleDecisions("previous", "back");
                                switch (decision) {
                                    case 0:
                                        currentPage--;
                                        break;
                                    case 1:
                                        return;
                                }
                            } else {
                                decision = promptMultipleDecisions("next", "previous", "back");
                                switch (decision) {
                                    case 0:
                                        currentPage++;
                                        break;
                                    case 1:
                                        currentPage--;
                                        break;
                                    case 2:
                                        return;
                                }
                            }
                        } else {
                            promptDecision("enter");
                            return;
                        }
                    }
                }else{
                    PrinterMenu.showLeads(new ArrayList<Lead>(), true, true);
                    promptDecision("enter");
                    return;
                }

            case "contacts":
                List<Contact> contactList = db.getAllContacts();
                if (contactList.size() > 0){
                    List<ArrayList<Contact>> listList = new ArrayList<>();
                    listList.add(new ArrayList<>());
                    for (Contact contact : contactList) {
                        if (currentIndex + Printer.numberOfTextRows(contact.toString()) < maxElements) {
                            currentIndex = currentIndex + Printer.numberOfTextRows(contact.toString());
                            listList.get(currentPage).add(contact);
                        } else {
                            listList.add(new ArrayList<>());
                            listList.get(++currentPage).add(contact);
                        }
                    }
                    currentPage = 0;
                    numPages = listList.size();
                    while (true) {
                        PrinterMenu.showContacts(listList.get(currentPage), currentPage == 0, currentPage + 1 == numPages, false);
                        if (listList.size() > 1) {
                            if (currentPage == 0) {
                                decision = promptMultipleDecisions("next", "back");
                                switch (decision) {
                                    case 0:
                                        currentPage++;
                                        break;
                                    case 1:
                                        return;
                                }
                            } else if (currentPage + 1 == numPages) {
                                decision = promptMultipleDecisions("previous", "back");
                                switch (decision) {
                                    case 0:
                                        currentPage--;
                                        break;
                                    case 1:
                                        return;
                                }
                            } else {
                                decision = promptMultipleDecisions("next", "previous", "back");
                                switch (decision) {
                                    case 0:
                                        currentPage++;
                                        break;
                                    case 1:
                                        currentPage--;
                                        break;
                                    case 2:
                                        return;
                                }
                            }
                        } else {
                            promptDecision("enter");
                            return;
                        }
                    }
                }else{
                    PrinterMenu.showContacts(new ArrayList<Contact>(), true, true, false);
                    promptDecision("enter");
                    return;
                }

            case "opportunities":
                List<Opportunity> opportunityList = db.getAllOpportunities();
                if (opportunityList.size() > 0){
                    List<ArrayList<Opportunity>> listList = new ArrayList<>();
                    listList.add(new ArrayList<>());
                    for (Opportunity opportunity : opportunityList) {
                        if (currentIndex + Printer.numberOfTextRows(opportunity.toString()) < maxElements) {
                            currentIndex = currentIndex + Printer.numberOfTextRows(opportunity.toString());
                            listList.get(currentPage).add(opportunity);
                        } else {
                            listList.add(new ArrayList<>());
                            listList.get(++currentPage).add(opportunity);
                        }
                    }
                    currentPage = 0;
                    numPages = listList.size();
                    while (true) {
                        PrinterMenu.showOpportunities(listList.get(currentPage), currentPage == 0, currentPage + 1 == numPages, false);
                        if (listList.size() > 1) {
                            if (currentPage == 0) {
                                decision = promptMultipleDecisions("next", "back");
                                switch (decision) {
                                    case 0:
                                        currentPage++;
                                        break;
                                    case 1:
                                        return;
                                }
                            } else if (currentPage + 1 == numPages) {
                                decision = promptMultipleDecisions("previous", "back");
                                switch (decision) {
                                    case 0:
                                        currentPage--;
                                        break;
                                    case 1:
                                        return;
                                }
                            } else {
                                decision = promptMultipleDecisions("next", "previous", "back");
                                switch (decision) {
                                    case 0:
                                        currentPage++;
                                        break;
                                    case 1:
                                        currentPage--;
                                        break;
                                    case 2:
                                        return;
                                }
                            }
                        } else {
                            promptDecision("enter");
                            return;
                        }
                    }
                }else{
                    PrinterMenu.showOpportunities(new ArrayList<Opportunity>(), true, true, false);
                    promptDecision("enter");
                    return;
                }

            case "accounts":
                List<Account> accountList = db.getAllAccounts();
                if (accountList.size() > 0){
                    List<ArrayList<Account>> listList = new ArrayList<>();
                    listList.add(new ArrayList<>());
                    for (Account account : accountList) {
                        if (currentIndex + Printer.numberOfTextRows(account.toString()) < maxElements) {
                            currentIndex = currentIndex + Printer.numberOfTextRows(account.toString());
                            listList.get(currentPage).add(account);
                        } else {
                            listList.add(new ArrayList<>());
                            listList.get(++currentPage).add(account);
                        }
                    }
                    currentPage = 0;
                    numPages = listList.size();
                    while (true) {
                        PrinterMenu.showAccounts(listList.get(currentPage), currentPage == 0, currentPage + 1 == numPages);
                        if (listList.size() > 1) {
                            if (currentPage == 0) {
                                decision = promptMultipleDecisions("next", "back");
                                switch (decision) {
                                    case 0:
                                        currentPage++;
                                        break;
                                    case 1:
                                        return;
                                }
                            } else if (currentPage + 1 == numPages) {
                                decision = promptMultipleDecisions("previous", "back");
                                switch (decision) {
                                    case 0:
                                        currentPage--;
                                        break;
                                    case 1:
                                        return;
                                }
                            } else {
                                decision = promptMultipleDecisions("next", "previous", "back");
                                switch (decision) {
                                    case 0:
                                        currentPage++;
                                        break;
                                    case 1:
                                        currentPage--;
                                        break;
                                    case 2:
                                        return;
                                }
                            }
                        } else {
                            promptDecision("enter");
                            return;
                        }
                    }
                }else{
                    PrinterMenu.showAccounts(new ArrayList<Account>(), true, true);
                    promptDecision("enter");
                    return;
                }

            default:
                throw new IllegalArgumentException("There is no implementation of show method to the object type " + objectType);
        }
    }

    // Method to create the menu showing all available Contacts in a List
    private void showContactsMenu(List<Contact> contactList) {
        int maxElements = PrinterMenu.getPrintMultipleObjectsMax();
        int currentPage = 0;
        int currentIndex = 0;
        int decision;
        // List of contacts is separated in multiple lists (pages)
        List<ArrayList<Contact>> listListContacts = new ArrayList<>();
        listListContacts.add(new ArrayList<>());

        for (Contact contact : contactList) {
            if (currentIndex++ < maxElements) {
                listListContacts.get(currentPage).add(contact);
            } else {
                listListContacts.add(new ArrayList<>());
                listListContacts.get(++currentPage).add(contact);
            }
        }

        // Allow user to change between the pages
        int numPages = listListContacts.size();
        while (true) {
            PrinterMenu.showContacts(listListContacts.get(currentPage), currentPage == 0, currentPage + 1 == numPages, true);
            if (listListContacts.size() > 1) {
                if (currentPage == 0) {
                    decision = promptMultipleDecisions("next", "back");
                    switch (decision) {
                        case 0:
                            currentPage++;
                            break;
                        case 1:
                            return;
                    }
                } else if (currentPage + 1 == numPages) {
                    decision = promptMultipleDecisions("previous", "back");
                    switch (decision) {
                        case 0:
                            currentPage--;
                            break;
                        case 1:
                            return;
                    }
                } else {
                    decision = promptMultipleDecisions("next", "previous", "back");
                    switch (decision) {
                        case 0:
                            currentPage++;
                            break;
                        case 1:
                            currentPage--;
                        case 2:
                            return;
                    }
                }
            } else {
                promptDecision("enter");
                return;
            }
        }
    }
    // Method to create the menu showing all available Opportunities in a List
    private void showOpportunitiesMenu(List<Opportunity> opportunityList) {
        int maxElements = PrinterMenu.getPrintMultipleObjectsMax();
        int currentPage = 0;
        int currentIndex = 0;
        int decision;
        // List of opportunities is separated in multiple lists (pages)
        List<ArrayList<Opportunity>> listListOpportunity = new ArrayList<>();
        listListOpportunity.add(new ArrayList<>());

        for (Opportunity opportunity : opportunityList) {
            if (currentIndex++ < maxElements) {
                listListOpportunity.get(currentPage).add(opportunity);
            } else {
                listListOpportunity.add(new ArrayList<>());
                listListOpportunity.get(++currentPage).add(opportunity);
            }
        }

        // Allow user to change between the pages
        int numPages = listListOpportunity.size();
        while (true) {
            PrinterMenu.showOpportunities(listListOpportunity.get(currentPage), currentPage == 0, currentPage + 1 == numPages, true);
            if (listListOpportunity.size() > 1) {
                if (currentPage == 0) {
                    decision = promptMultipleDecisions("next", "back");
                    switch (decision) {
                        case 0:
                            currentPage++;
                            break;
                        case 1:
                            return;
                    }
                } else if (currentPage + 1 == numPages) {
                    decision = promptMultipleDecisions("previous", "back");
                    switch (decision) {
                        case 0:
                            currentPage--;
                            break;
                        case 1:
                            return;
                    }
                } else {
                    decision = promptMultipleDecisions("next", "previous", "back");
                    switch (decision) {
                        case 0:
                            currentPage++;
                            break;
                        case 1:
                            currentPage--;
                        case 2:
                            return;
                    }
                }
            } else {
                promptDecision("enter");
                return;
            }
        }
    }

    //Method that handles the prompts to convert a lead
    private void promptConvert(int id) {
        // check if Lead exists, if not print error message
        if (db.hasLead(id)) {
            String contactName = db.getLeadRepository().getById(id).getName();
            //call methods to prompt Opportunity's product and quantity
            PrinterMenu.printMenu("convert");
            Product product = promptProduct();
            PrinterMenu.printMenu("convert", "product", product.toString());
            int quantity = promptPositiveNumber();
            //print also the contact (from the lead's info)
            PrinterMenu.printMenu("convert", "quantity and contact", Integer.toString(quantity), contactName);
            if (!promptDecision("enter back")){
                return;
            }
            PrinterMenu.printMenu("convert", "account_select", Integer.valueOf(db.getAllAccounts().size()).toString());
            int decision;
            if (db.getAllAccounts().size() == 0){
                if (promptDecision("enter back")){
                    decision = 0;
                }else {
                    return;
                }
            }else {
                decision = promptMultipleDecisions("y", "n", "back");
            }
            switch (decision) {
                case 0:
                    //call methods to prompt Account's industry, employee count, city and country
                    PrinterMenu.printMenu("convert", "account");
                    Industry industry = promptIndustry();
                    PrinterMenu.printMenu("convert", "industry", industry.toString());
                    int employeeCount = promptPositiveNumber();
                    PrinterMenu.printMenu("convert", "employees", Integer.toString(employeeCount));
                    String city = promptString("location");
                    PrinterMenu.printMenu("convert", "city", city);
                    String country = promptString("location");
                    PrinterMenu.printMenu("convert", "country", country);
                    if (promptDecision("enter back")) {
                        db.convertLead(id, product, quantity, industry, employeeCount, city, country);
                    }
                    break;
                case 1:
                    PrinterMenu.printMenu("convert", "account_id");
                    Integer accountId = promptId("account");
                    Account account = db.getAccountById(accountId);
                    if (account != null){
                        PrinterMenu.printMenu("convert","account_id", accountId.toString(),
                                account.getIndustry().toString(), Integer.valueOf(account.getEmployeeCount()).toString()
                                , account.getCity(), account.getCountry());
                        if (promptDecision("enter back")) {
                            db.convertLead(id, product, quantity, accountId);
                        }
                    }else{
                        PrinterMenu.setWarning("Error: Account could not be fetched!");
                    }
                    break;
            }

        } else {
            PrinterMenu.setWarning("There is no lead with id " + id + " to convert!");
        }
    }

    // Method that handles the prompts to create a new lead
    private void promptLead() {
        // call methods to prompt name, phone number, email and company name
        PrinterMenu.printMenu("lead");
        String name = promptString("name");
        PrinterMenu.printMenu("lead", "name", name);
        String phoneNumber = promptString("phone");
        phoneNumber = phoneNumber.replaceAll(" ", "");
        PrinterMenu.printMenu("lead", "phone", phoneNumber);
        String email = promptString("email");
        PrinterMenu.printMenu("lead", "email", email);
        String companyName = promptString("");
        PrinterMenu.printMenu("lead", "company", companyName);
        if (promptDecision("enter back")) {
            db.addLead(name, phoneNumber, email, companyName);
        }
    }

    // Method to ask for the user decision - one or two outcomes
    private boolean promptDecision(String decision) {
        String input;
        switch (decision) {
            case "enter back":
                do {
                    input = scanner.nextLine().trim().toLowerCase();
                    switch (input) {
                        case "":
                            return true;
                        case "back":
                            return false;
                    }
                    PrinterMenu.setWarning("Please input a valid command from the highlighted above!");
                    PrinterMenu.printMenu("");
                    PrinterMenu.clearWarning();
                } while (true);
            case "enter":
                scanner.nextLine();
                return true;
            case "exit":
                do {
                    input = scanner.nextLine().trim().toLowerCase();
                    switch (input) {
                        case "":
                            return true;
                        case "exit":
                            return false;
                    }
                    PrinterMenu.setWarning("Please input a valid command from the highlighted above!");
                    PrinterMenu.printMenu("");
                    PrinterMenu.clearWarning();
                } while (true);
        }
        return false;
    }

    // Method to ask for the user decision - more than 2 outcomes
    private int promptMultipleDecisions(String... choices){
        if (choices.length == 0) {
            throw new IllegalArgumentException();
        }
        String input;
        while (true) {
            input = scanner.nextLine().trim().toLowerCase();
            for (int i = 0; i < choices.length; i++) {
                if (input.equals(choices[i].trim().toLowerCase())) {
                    return i;
                }
            }
            PrinterMenu.setWarning("Please input a valid command from the highlighted above!");
            PrinterMenu.printMenu("");
            PrinterMenu.clearWarning();
        }
    }

    // prompt Product and validate
    private Product promptProduct() {
        String input;
        input = scanner.nextLine().trim().toUpperCase();
        while (!validProduct(input)) {
            PrinterMenu.setWarning("Please input a valid Product option!");
            PrinterMenu.printMenu("");
            PrinterMenu.clearWarning();
            input = scanner.nextLine().trim().toUpperCase();
        }
        return Product.valueOf(input);
    }

    //prompt Industry and validate
    private Industry promptIndustry() {
        String input;
        input = scanner.nextLine().trim().toUpperCase();
        while (!validIndustry(input)) {
            PrinterMenu.setWarning("Please input a valid Industry option!");
            PrinterMenu.printMenu("");
            PrinterMenu.clearWarning();
            input = scanner.nextLine().trim().toUpperCase();
        }
        return Industry.valueOf(input);
    }

    //prompt number and validate
    private int promptPositiveNumber() {
        String input = scanner.nextLine().trim();
        while (!isValidPositiveNumber(input)) {
            PrinterMenu.setWarning("Please input a valid integer number! Must be positive!");
            PrinterMenu.printMenu("");
            PrinterMenu.clearWarning();
            input = scanner.nextLine().trim();
        }
        return Integer.parseInt(input);
    }

    //prompt phone number / email / name / location and validate
    private String promptString(String checkCondition) {
        String input;
        switch (checkCondition) {
            case "phone":
                input = scanner.nextLine().trim();
                while (!validPhone(input)) {
                    PrinterMenu.setWarning("Please input a valid Phone Number!");
                    PrinterMenu.printMenu("");
                    PrinterMenu.clearWarning();
                    input = scanner.nextLine().trim();
                }
                return input;
            case "email":
                input = scanner.nextLine().trim();
                while (!validEmail(input)) {
                    PrinterMenu.setWarning("Please input a valid Email!");
                    PrinterMenu.printMenu("");
                    PrinterMenu.clearWarning();
                    input = scanner.nextLine().trim();
                }
                return input;
            case "name":
                input = scanner.nextLine().trim();
                while (!validName(input)) {
                    PrinterMenu.setWarning("Please input a valid Name!");
                    PrinterMenu.printMenu("");
                    PrinterMenu.clearWarning();
                    input = scanner.nextLine().trim();
                }
                return input;
            case "location":
                input = scanner.nextLine().trim();
                while (!validLocation(input)) {
                    PrinterMenu.setWarning("Please input a valid location (case sensitive)!");
                    PrinterMenu.printMenu("");
                    PrinterMenu.clearWarning();
                    input = scanner.nextLine().trim();
                }
                return input;
            default:
                input = scanner.nextLine().trim();
                while (input.isEmpty()) {
                    PrinterMenu.setWarning("Please input a non empty string!");
                    PrinterMenu.printMenu("");
                    PrinterMenu.clearWarning();
                    input = scanner.nextLine().trim();
                }
                return input;
        }
    }
    //prompt id
    private Integer promptId(String condition){
        int id;
        switch (condition){
            case "account":
                id = promptPositiveNumber();
                while (!db.hasAccount(id)) {
                    PrinterMenu.setWarning("Please input the id of an existing Account!");
                    PrinterMenu.printMenu("");
                    PrinterMenu.clearWarning();
                    id = promptPositiveNumber();
                }
                return id;
            case "opportunity":
                id = promptPositiveNumber();
                while (!db.hasOpportunity(id)) {
                    PrinterMenu.setWarning("Please input the id of an existing Opportunity!");
                    PrinterMenu.printMenu("");
                    PrinterMenu.clearWarning();
                    id = promptPositiveNumber();
                }
                return id;
            case "contact":
                id = promptPositiveNumber();
                while (!db.hasContact(id)) {
                    PrinterMenu.setWarning("Please input the id of an existing Contact!");
                    PrinterMenu.printMenu("");
                    PrinterMenu.clearWarning();
                    id = promptPositiveNumber();
                }
                return id;
            case "lead":
                id = promptPositiveNumber();
                while (!db.hasLead(id)) {
                    PrinterMenu.setWarning("Please input the id of an existing Lead!");
                    PrinterMenu.printMenu("");
                    PrinterMenu.clearWarning();
                    id = promptPositiveNumber();
                }
                return id;
            case "salesRep":
                id = promptPositiveNumber();
                while (!db.hasSalesRep(id)) {
                    PrinterMenu.setWarning("Please input the id of an existing SalesRep!");
                    PrinterMenu.printMenu("");
                    PrinterMenu.clearWarning();
                    id = promptPositiveNumber();
                }
                return id;
            default:
                throw new IllegalArgumentException("The condition " + condition + " is not implemented!");
        }
    }
}
