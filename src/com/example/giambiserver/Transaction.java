package com.example.giambiserver;

import java.util.Map;
import java.util.Set;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * This class handles all the CRUD operations related to Transaction entity.
 * 
 */
public class Transaction {

    /**
     * Creates or updates a transaction.
     * 
     * @param key
     * @param map
     * @return true if a transaction is changed or created, false otherwise.
     */
    public static long createOrUpdateTransaction(long id,
            Map<String, String> map) {
        Entity transaction = getTransaction(id);
        if (transaction == null) {
            transaction = new Entity("Transaction");
        }

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
                
                // Persists the transaction
                Util.persistEntity(transaction);
                return transaction.getKey().getId();
            }
        }
        return 0;
    }

    /**
     * Return all the Transactions
     * 
     * @param kind
     * @return transactions
     */
    public static Iterable<Entity> getAllTransactions() {
        return Util.listEntities("Transaction", null, null);
    }

    /**
     * Return all transactions for a particular username
     * 
     * @param String
     *            username
     * @return List<Entity> transactions
     */
    public static Iterable<Entity> getAllUserTransactions(String username) {
        return Util.listEntities("Transaction", "username", username);
    }

    /**
     * Return all transactions for a particular account under a user
     * 
     * @param String
     *            account
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
     * @param productKey
     *            : product to be deleted
     * @return status string
     */
    public static String deleteProduct(long id) {
        Key key = KeyFactory.createKey("Transaction", id);
        Util.deleteEntity(key);
        return "Product deleted successfully";

    }
}
