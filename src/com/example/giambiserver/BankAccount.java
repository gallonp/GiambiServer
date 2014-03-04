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
//			Entity bankAccount = getBankAccountList(bankAccountNumber);
//			if (bankAccount == null) {
		    Entity bankAccount = new Entity("BankAccount");
		    bankAccount.setProperty("bankAccountName", bankAccountName);
			bankAccount.setProperty("bankAccountNumber", bankAccountNumber);
			bankAccount.setProperty("userAccount", userAccount);
			bankAccount.setProperty("bankName", bankName);
			bankAccount.setProperty("balance", balance);
//			} else {
//				return false;
//			}
			Util.persistEntity(bankAccount);
			return true;
		}
		
//		public static boolean updateBankAccount(String bankAccountName) {
//			Entity bankAccount = getSingleBankAccount(bankAccountName);
//			if (bankAccount == null) {
//				return false;
//			} else {
//				bankAccount.setProperty("bankAccountName", bankAccountName);
//			}
//			Util.persistEntity(bankAccount);
//			return false;
//		}

		public static List<Entity> getBankAccountList(String userAccount) {
			Query query = new Query("BankAccount");
			Filter bankAccountFilter = new Query.FilterPredicate("userAccount", FilterOperator.EQUAL, userAccount);
			query.setFilter(bankAccountFilter);
			List<Entity> results = Util.getDatastoreServiceInstance()
					.prepare(query).asList(FetchOptions.Builder.withDefaults());
//			if (!results.isEmpty()) {
				return results;
//			}
		}
	}
