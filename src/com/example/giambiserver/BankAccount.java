package com.example.giambiserver;

import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class BankAccount {
		
		public static boolean createBankAccount(String bankAccountNumber, String bankAccountName,
				String userAccount, String bankName, String balance) {
			Entity bankAccount = getSingleBankAccount(bankAccountNumber);
			if (bankAccount == null) {
				bankAccount = new Entity("BankAccount");
				bankAccount.setProperty("bankAccountName", bankAccountName);
				bankAccount.setProperty("bankAccountNumber", bankAccountNumber);
				bankAccount.setProperty("userAccount", userAccount);
				bankAccount.setProperty("bankName", bankName);
				bankAccount.setProperty("balance", balance);
			} else {
				return false;
			}
			Util.persistEntity(bankAccount);
			return true;
		}
		
		public static boolean updateBankAccount(String bankAccountName) {
			Entity userAccount = getSingleBankAccount(bankAccountName);
			if (userAccount == null) {
				return false;
			} else {
				userAccount.setProperty("bankAccountName", bankAccountName);
			}
			Util.persistEntity(userAccount);
			return false;
		}

		public static Entity getSingleBankAccount(String bankAccountName) {
			Query query = new Query("BankAccount");
			Filter singleBankAccountFilter = new Query.FilterPredicate("bankAccountName", FilterOperator.EQUAL, bankAccountName);
			query.setFilter(singleBankAccountFilter);
			List<Entity> results = Util.getDatastoreServiceInstance()
					.prepare(query).asList(FetchOptions.Builder.withDefaults());
			if (!results.isEmpty()) {
				return (Entity) results.remove(0);
			}
			return null;
		}
	}
