package com.example.giambiserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.logging.Logger;
import java.util.Calendar;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.appengine.api.datastore.Entity;
/**
 * Handles requests of creating new bank account under
 * the currently logged in user account.
 * 
 * This method prints a message once a new cookie is created.
 * 
 * Note: the cookie verify part is not completed.
 *       Entity in req still needed.
 *       
 * @author Wen Xin
 * @version 1.6 Last authored 2/28/14
 */
public class CreateAccountServlet extends HttpServlet {
	
			private boolean createSuccess, doNext;
		
			private static final Logger logger = Logger
					.getLogger(CreateAccountServlet.class.getCanonicalName());
				
			public void doPost(HttpServletRequest req, HttpServletResponse resp)
					throws IOException {
				String data = req.getParameter("json");
				String decodedContent = "";
				if (data != null) {
					decodedContent = URLDecoder.decode(data, "UTF-8");
				} else {
					throw new IOException("Data is null.");
				}
				JSONObject job = (JSONObject) JSONValue.parse(decodedContent);
				resp.getWriter().print(decodedContent);
				String bankAccountNumber = (String) job.get("bankAccountNumber");
				String bankAccountName = (String) job.get("bankAccountName");
				String userAccount = (String) job.get("userAccount");
				String bankName = (String) job.get("bankName");
				String balance = (String) job.get("balance");
				resp.setContentType("text/plain");
				PrintWriter out = resp.getWriter();
				Calendar myCal = Calendar.getInstance();
				
				if (!bankAccountNumber.isEmpty() && !bankAccountName.isEmpty() &&
						!userAccount.isEmpty() && !bankName.isEmpty() && !balance.isEmpty()) {
					Entity sessionCookie = SessionCookie.getSessionCookie(userAccount);
					if (sessionCookie == null) {
						String cookieValue = Long.toHexString(myCal.getTimeInMillis());
						Cookie newCookie = new Cookie("id", cookieValue);
						newCookie.setMaxAge(300);
						SessionCookie.createSessionCookie(userAccount, newCookie);
						out.println("Cookie created. \"name\"=\"id\", \"value\"=\"cookieValue\"");
						doNext = true;
					} else if (SessionCookie.varifySessionCookie(req, userAccount)) {
						out.println("Cookie varified.");
						doNext = true;
					} else /*if sessionCookie != null yet not varified*/ {
							out.println("Your session expired.");
							Entity toDelete = SessionCookie.getSessionCookie(userAccount);
							Util.deleteEntity(toDelete.getKey());
							doNext = false;
					}
					System.out.println("doNext: " + doNext);
					if (true) {
						boolean toCreate = BankAccount.verifyDuplicateBankAccount(bankAccountNumber, bankName);
						System.out.println(toCreate);
						if (toCreate) {
							if(createSuccess = BankAccount.createBankAccount(bankAccountNumber,
									bankAccountName, userAccount, bankName, balance)) {
								out.println("New account created.\nBank name: " + bankName
										+ "\nAccountNumber: " + bankAccountNumber);
							} else {
								out.println("Bank account not created.");
							}
						} else {
							out.println("Bank account not created: duplicate.");
						}
					}
				}
			}
		}
