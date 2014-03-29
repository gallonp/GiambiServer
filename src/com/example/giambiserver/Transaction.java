package com.example.giambiserver;

import java.util.*;
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
    private static final Logger logger = Logger
            .getLogger(Transaction.class.getCanonicalName());
    /**
     * Creates or updates a transaction.
     * 
     * @param id
     * @param map
     * @return true if a transaction is changed or created, false otherwise.
     */
    public static long createOrUpdateTransaction(long id,
            Map<String, String> map) throws IllegalBankAccountException{
        Entity transaction = getTransaction(id);
        if (transaction == null) {
            transaction = new Entity("Transaction");
        }
        logger.log(Level.INFO,transaction.getKey().toString());

        if (map != null) {
            Set<String> properties = map.keySet();
            for (String property : properties) {
                transaction.setProperty(property, map.get(property));
            }

            // Gets information for updating bank account balance.
            String username = map.get("username");
            String accountNumber = map.get("accountNumber");
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
     * Return all the Transactions
     * 
     * @return transactions
     */
    public static Iterable<Entity> getAllTransactions() {
        return Util.listEntities("Transaction", null, null);
    }

    /**
     * Return all transactions for a particular username
     * @param username
     * @return iterable transactions
     */
    public static Iterable<Entity> getAllTransactions(String username) {
        return Util.listEntities("Transaction", "username", username);
    }

    /**
     * Return all transactions for a particular username
     * 
     * @return List<Entity> transactions
     */
    public static List<Entity> getTransactionList(String username) {
        Iterator<Entity> itr = Util.listEntities("Transaction", "username", username).iterator();
        List<Entity> list = new LinkedList<>();
        while (itr.hasNext()) {
            list.add(itr.next());
        }
        return list;
    }

    /**
     * Return all transactions for a particular account under a user
     *
     * @return List<Entity> transactions
     */
    public static Iterable<Entity> getAccountTransactions(String username,
            String accountNumber) {
        return Util.listEntitiesFilters("Transaction", "username", username,
                "accountNumber", accountNumber);
    }

    /**
     * Get transaction entity
     * 
     * @param id
     *            of transaction
     * @return: Transaction entity
     */
    public static Entity getTransaction(long id) {
        Key key = KeyFactory.createKey("Transaction", id);
        return Util.findEntity(key);
    }

    /**
     * Delete product entity
     *
     * @return status string
     */
    public static String deleteProduct(long id) {
        Key key = KeyFactory.createKey("Transaction", id);
        Util.deleteEntity(key);
        return "Product deleted successfully";

    }
}
