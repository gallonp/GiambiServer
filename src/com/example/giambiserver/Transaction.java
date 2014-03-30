package com.example.giambiserver;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * This class handles all the CRUD operations related to Transaction entity.
 * 
 */
public class Transaction {
    /**
     * 
     */
    private static final Logger LOGGER = Logger.getLogger(Transaction.class
            .getCanonicalName());
    /**
     * 
     */
    private static final String TRANSACTION_STRING = "Transaction";
    /**
     * 
     */
    private static final String USERNAME_STRING = "username";
    /**
     * 
     */
    private static final String ACCOUNTNUMBER_STRING = "accountNumber";
    
    /**
     * Creates or updates a transaction.
     * 
     * @param id 
     * @param map 
     * @return true if a transaction is changed or created, false otherwise.
     * @throws IllegalBankAccountException 
     */
    public static long createOrUpdateTransaction(long id,
            Map<String, String> map) throws IllegalBankAccountException {
        Entity transaction = getTransaction(id);
        if (transaction == null) {
            transaction = new Entity(TRANSACTION_STRING);
        }
        LOGGER.log(Level.INFO, transaction.getKey().toString());

        if (map != null) {
            Set<String> properties = map.keySet();
            for (String property : properties) {
                transaction.setProperty(property, map.get(property));
            }

            // Gets information for updating bank account balance.
            String username = map.get(USERNAME_STRING);
            String accountNumber = map.get(ACCOUNTNUMBER_STRING);
            String amount = map.get("amount");

            // Updates bank account if the transaction is not completed with
            // cash.
            if (!accountNumber.equalsIgnoreCase("cash")) {
                BankAccount.updatesBalance(username, accountNumber, amount);
            }

            // Persists the transaction
            Util.persistEntity(transaction);
            return transaction.getKey().getId();
        }
        return 0;
    }

    /**
     * Return all the Transactions.
     * 
     * @return transactions
     */
    public static Iterable<Entity> getAllTransactions() {
        return Util.listEntities(TRANSACTION_STRING, null, null);
    }

    /**
     * Return all transactions for a particular username.
     * 
     * @return iterable transactions
     * @param username 
     */
    public static Iterable<Entity> getAllTransactions(String username) {
        return Util.listEntities(TRANSACTION_STRING, USERNAME_STRING, username);
    }

    /**
     * Return all transactions for a particular username.
     * 
     * @return List<Entity> transactions
     * @param username 
     */
    public static List<Entity> getTransactionList(String username) {
        Iterator<Entity> itr = Util.listEntities(TRANSACTION_STRING, USERNAME_STRING,
                username).iterator();
        List<Entity> list = new LinkedList<>();
        while (itr.hasNext()) {
            list.add(itr.next());
        }
        return list;
    }

    /**
     * Return all transactions for a particular account under a user.
     * 
     * @return List<Entity> transactions
     * @param username 
     * @param accountNumber 
     */
    public static Iterable<Entity> getAccountTransactions(String username,
            String accountNumber) {
        return Util.listEntitiesFilters(TRANSACTION_STRING, USERNAME_STRING, username,
                ACCOUNTNUMBER_STRING, accountNumber);
    }

    /**
     * Get transaction entity.
     * 
     * @param id
     *            of transaction
     * @return Transaction entity
     */
    public static Entity getTransaction(long id) {
        Key key = KeyFactory.createKey(TRANSACTION_STRING, id);
        return Util.findEntity(key);
    }

    /**
     * Delete product entity.
     * 
     * @return status string
     * @param id 
     */
    public static String deleteTransaction(long id) {
        Key key = KeyFactory.createKey(TRANSACTION_STRING, id);
        Util.deleteEntity(key);
        return "Transaction deleted successfully";
    }
}
