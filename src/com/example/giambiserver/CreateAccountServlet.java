package com.example.giambiserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.logging.Logger;

import javax.servlet.http.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

@SuppressWarnings("serial")
public class CreateAccountServlet extends HttpServlet {
	
		private static final Logger logger = Logger
				.getLogger(CreateAccountServlet.class.getCanonicalName());
			
		public void doPost(HttpServletRequest req, HttpServletResponse resp)
				throws IOException {

			String data = req.getParameter("json");
			String decodedContent = "";
			if (data != null) {
				decodedContent = URLDecoder.decode(data, "UTF-8");
			} else {
				throw new IOException("Data illegal.");
			}
			JSONObject job = (JSONObject) JSONValue.parse(decodedContent);
			resp.getWriter().print(decodedContent);
			String set = job.toJSONString();
			String bankAccountNumber = (String) job.get("bankAccountNumber");
			String bankAccountName = (String) job.get("bankAccountName");
			String userAccount = (String) job.get("userAccount");
			String bankName = (String) job.get("bankName");
			String balance = (String) job.get("balance");
			//Check if cookie is valid and not expired
			//if (SessionCookie.verifySessionCookie(req,userAccount)){
				//Needs username not userAccount
			//}
			resp.setContentType("text/plain");
			PrintWriter out = resp.getWriter();
			Boolean createSuccess = false;
			if (!bankAccountNumber.isEmpty() && !bankAccountName.isEmpty() &&
					!userAccount.isEmpty() && !bankName.isEmpty() && !balance.isEmpty()) {
				createSuccess = BankAccount.createBankAccount(bankAccountNumber, bankAccountName, userAccount,
						bankName, balance);
			} else {
				out.print("Invalid request: missing credential");
			}
			if (createSuccess) {
				out.print("Successfully created account: " + bankAccountNumber);
			} else {
				out.print("Failed to create bankAccout. ");
			}
		}
	}
