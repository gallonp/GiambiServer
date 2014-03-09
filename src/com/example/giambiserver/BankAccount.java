package com.example.giambiserver;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class BankAccount {
    private static final Logger logger = Logger.getLogger(BankAccount.class
            .getCanonicalName());

    public static boolean createBankAccount(String bankAccountNumber,
            String bankAccountName, String userAccount, String bankName,
            String balance) {
        // Entity bankAccount = getBankAccountList(bankAccountNumber);
        // if (bankAccount == null) {
        Entity bankAccount = new Entity("BankAccount");
        bankAccount.setProperty("bankAccountName", bankAccountName);
        bankAccount.setProperty("bankAccountNumber", bankAccountNumber);
        bankAccount.setProperty("userAccount", userAccount);
        bankAccount.setProperty("bankName", bankName);
        bankAccount.setProperty("balance", balance);
        // } else {
        // return false;
        // }
        Util.persistEntity(bankAccount);
        return true;
    }

    // public static boolean updateBankAccount(String bankAccountName) {
    // Entity bankAccount = getSingleBankAccount(bankAccountName);
    // if (bankAccount == null) {
    // return false;
    // } else {
    // bankAccount.setProperty("bankAccountName", bankAccountName);
    // }
    // Util.persistEntity(bankAccount);
    // return false;
    // }

    /**
     * Updates balance of a bank account based on the username's account number
     * and amount. Added by Haoli.
     * 
     * @param String username
     * @param String accountNumber
     * @param String amount
     * @throws IllegalArgumentException if the account number is not valid
     * @return none.
     */
    public static void updatesBalance(String username, String accountNumber,
            String amount) {
        List<Entity> bankAccounts = BankAccount.getBankAccountList(username);
        for (Entity bankAccount : bankAccounts) {
            if (((String) bankAccount.getProperty("bankAccountNumber"))
                    .equalsIgnoreCase(accountNumber)) {
                Double balance = Double.parseDouble((String) bankAccount
                        .getProperty("balance"));
                balance = balance - Double.parseDouble(amount);
                bankAccount.setProperty("balance", balance.toString());
                Util.persistEntity(bankAccount);
            }
        }
        logger.log(Level.WARNING, "Bank account: " + accountNumber
                + " not found. Balance NOT updated.");
        throw new IllegalArgumentException("illegal Bank account: " + accountNumber
                + " not found. Balance NOT updated.");
    }

    public static List<Entity> getBankAccountList(String userAccount) {
        Query query = new Query("BankAccount");
        Filter bankAccountFilter = new Query.FilterPredicate("userAccount",
                FilterOperator.EQUAL, userAccount);
        query.setFilter(bankAccountFilter);
        List<Entity> results = Util.getDatastoreServiceInstance()
                .prepare(query).asList(FetchOptions.Builder.withDefaults());
        // if (!results.isEmpty()) {
        return results;
        // }
    }
}
