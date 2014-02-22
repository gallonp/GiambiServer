package com.example.giambiserver;

import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class UserAccount {

	public static boolean createUserAccount(String username, String password) {
		Entity userAccount = getSingleUser(username);
		if (userAccount == null) {
			userAccount = new Entity("UserAccount");
			userAccount.setProperty("username", username);
			userAccount.setProperty("password", password);
		} else {
			return false;
		}
		Util.persistEntity(userAccount);
		return true;
	}

	public static boolean updateUserAccount(String username, String password) {
		Entity userAccount = getSingleUser(username);
		if (userAccount == null) {
			return false;
		} else {
			userAccount.setProperty("password", password);
		}
		Util.persistEntity(userAccount);
		return false;
	}

	/**
	 * get Item with item name
	 * 
	 * @param itemName
	 *            : get itemName
	 * @return item entity
	 */
	public static Entity getSingleUser(String username) {
		Query query = new Query("UserAccount");
		Filter singleUserFilter = new Query.FilterPredicate("username", FilterOperator.EQUAL, username);
		query.setFilter(singleUserFilter);
		List<Entity> results = Util.getDatastoreServiceInstance()
				.prepare(query).asList(FetchOptions.Builder.withDefaults());
		if (!results.isEmpty()) {
			return (Entity) results.remove(0);
		}
		return null;
	}

}
