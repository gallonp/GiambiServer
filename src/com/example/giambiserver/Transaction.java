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
    public static boolean createOrUpdateTransaction(String key,
            Map<String, String> map) {
        Entity transaction = getTransaction(key);
        if (transaction == null) {
            transaction = new Entity("Transaction");
        }

        if (map != null) {
            Set<String> properties = map.keySet();
            for (String property : properties) {
                transaction.setProperty(property, map.get(property));
            }
            Util.persistEntity(transaction);
            return true;
        }
        return false;
    }

    /**
     * Return all the Transactions
     * 
     * @param kind
     *            : of kind product
     * @return products
     */
    public static Iterable<Entity> getAllTransactions() {
        return Util.listEntities("Transaction", null, null);
    }

    /**
     * Get transaction entity
     * 
     * @param id
     *            of transaction
     * @return: Transaction entity
     */
    public static Entity getTransaction(String id) {
        Key key = KeyFactory.stringToKey(id);
        return Util.findEntity(key);
    }

    /**
     * Delete product entity
     * 
     * @param productKey
     *            : product to be deleted
     * @return status string
     */
    public static String deleteProduct(String id) {
        Key key = KeyFactory.stringToKey(id);
        Util.deleteEntity(key);
        return "Product deleted successfully";

    }
}
