package com.ironhack.homework3.repository;

import com.ironhack.homework3.dao.classes.Account;
import com.ironhack.homework3.dao.classes.Contact;
import com.ironhack.homework3.dao.classes.Opportunity;
import com.ironhack.homework3.dao.queryInterfaces.IOpportunityCountryOrCityCount;
import com.ironhack.homework3.dao.queryInterfaces.IOpportunityIndustryCount;
import com.ironhack.homework3.enums.Industry;
import com.ironhack.homework3.enums.Product;
import com.ironhack.homework3.enums.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OpportunityRepositoryTest {

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        var contact = new Contact("Ben", "123643543", "Ben@BenIndustries.com", "Ben Industries");
        contactRepository.save(contact);
        var account = new Account(Industry.ECOMMERCE, 200, "London", "UK");
        accountRepository.save(account);
        var opportunity = new Opportunity(Product.HYBRID, 3000, contact, Status.OPEN, account);
        opportunityRepository.save(opportunity);
    }

    @AfterEach
    void tearDown() {
        opportunityRepository.deleteAll();
        contactRepository.deleteAll();
    }


    // ============================== JAVA Object Testing ==============================
    @Test
    void testToString() {
        Contact c = new Contact("John Smith", "2460247246", "johnthewarrior@fighters.com", "The smiths");
        contactRepository.save(c);
        Opportunity o = new Opportunity(Product.HYBRID, 30000, c, Status.OPEN);
        opportunityRepository.save(o);
        assertEquals("Id: 2, Product: HYBRID, Quantity: 30000, Decision Maker: John Smith, Status: OPEN", o.toString());
    }


    // ============================== CRUD Testing ==============================
    @Test
    void saveANewContact() {
        var OpportunityCountBeforeSave = opportunityRepository.count();
        var contact = new Contact("Macho Man", "123643543", "Randy@savage.com", "WWF");
        contactRepository.save(contact);
        var opportunity = new Opportunity(Product.HYBRID, 30000, contact, Status.CLOSED_WON);
        opportunityRepository.save(opportunity);
        var OpportunityCountAfterSave = opportunityRepository.count();
        assertEquals(1, OpportunityCountAfterSave - OpportunityCountBeforeSave);
    }


    // ============================== Custom Queries Testing ==============================
    // ==================== 7 - Reporting Functionality Quantity States ====================
    @Test
    void testMeanQuantity() {
        var o2 = new Opportunity(Product.HYBRID, 73, Status.OPEN);
        var o3 = new Opportunity(Product.HYBRID, 386, Status.OPEN);
        var o4 = new Opportunity(Product.HYBRID, 3468, Status.OPEN);
        opportunityRepository.saveAll(List.of(o2, o3, o4));
//         mean is from the values of setup (3000) and this new account
        assertEquals(((double) Math.round(((3000 + 73 + 386 + 3468) / 4.0) * 10000d) / 10000d), opportunityRepository.meanQuantity());
    }

    @Test
    void testOrderedListOfQuantities() {
        var o2 = new Opportunity(Product.HYBRID, 386, Status.OPEN);
        var o3 = new Opportunity(Product.HYBRID, 73, Status.OPEN);
        var o4 = new Opportunity(Product.HYBRID, 3468, Status.OPEN);
        opportunityRepository.saveAll(List.of(o2, o3, o4));
        // mean is from the values of setup (3000) and this new account
        assertEquals(List.of(73, 386, 3000, 3468), opportunityRepository.orderedListOfQuantities());
    }

    @Test
    void testMinQuantity() {
        var o2 = new Opportunity(Product.HYBRID, 73, Status.OPEN);
        var o3 = new Opportunity(Product.HYBRID, 386, Status.OPEN);
        var o4 = new Opportunity(Product.HYBRID, 3468, Status.OPEN);
        opportunityRepository.saveAll(List.of(o2, o3, o4));
        // min is from the values of setup (3000) and this new account
        assertEquals(73, opportunityRepository.minQuantity());
    }

    @Test
    void testMaxQuantity() {
        var o2 = new Opportunity(Product.HYBRID, 73, Status.OPEN);
        var o3 = new Opportunity(Product.HYBRID, 386, Status.OPEN);
        var o4 = new Opportunity(Product.HYBRID, 3468, Status.OPEN);
        opportunityRepository.saveAll(List.of(o2, o3, o4));
        opportunityRepository.save(o3);
        // max is from the values of setup (3000) and this new account
        assertEquals(3468, opportunityRepository.maxQuantity());
    }


    // ====================  ====================

}

