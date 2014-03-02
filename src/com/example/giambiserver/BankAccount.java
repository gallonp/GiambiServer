package com.example.giambiserver;

import java.util.List;
import java.util.ArrayList;

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
		
		/**
		 * Verify if there's a duplicate account in database.
		 * A new account is considered a duplicate if it has the same bank account
		 * number and bank name with any bank account already in the database
		 * 
		 * Note that in this method sameBankAccountNumber.size() should always < 2
		 * if data were handled decently, because duplicate accounts aren't allowed
		 * in the database
		 * 
		 * Possible update: throw an exception complaining illegal manipulation of data
		 *                  when user synchronizes
		 *                  
		 * Report bug to: leonardhsin@gmail.com
		 * 
		 * @param bankAccountNumber  the new bank account number to be verified
		 * @param bankName  the bank that holds the new bank account
		 * @return true if new account is judged as NOT a duplicate
		 *         false if new account is judged as a duplicate
		 */
		public static boolean verifyDuplicateBankAccount(String bankAccountNumber, String bankName) {
			Query allBankAccount1 = new Query("BankAccount");
			Filter verifyAccountNumber = new Query.FilterPredicate("bankAccountNumber", FilterOperator.EQUAL, bankAccountNumber);
			allBankAccount1.setFilter(verifyAccountNumber);
			List<Entity> sameBankAccountNumber = Util.getDatastoreServiceInstance()
					.prepare(allBankAccount1).asList(FetchOptions.Builder.withDefaults());
			Query allBankAccount2 = new Query("BankAccount");
			Filter verifyBankName = new Query.FilterPredicate("bankName", FilterOperator.EQUAL, bankName);
			allBankAccount2.setFilter(verifyBankName);
			List<Entity> sameBankName = Util.getDatastoreServiceInstance()
					.prepare(allBankAccount2).asList(FetchOptions.Builder.withDefaults());
			if (sameBankName.size() != 0 && sameBankName.containsAll(sameBankAccountNumber)
					&& sameBankAccountNumber.size() != 0) {
				return false;
			}
			return true;
		}
	}
