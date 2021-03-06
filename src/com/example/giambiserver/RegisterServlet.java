package com.example.giambiserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

@SuppressWarnings("serial")
public class RegisterServlet extends HttpServlet {
	private static final Logger logger = Logger
			.getLogger(RegisterServlet.class.getCanonicalName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		}
		
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		// Need parse safty check!!!!!
		String data = req.getParameter("json");
		String decodedContent;
		if (data != null) {
			decodedContent = URLDecoder.decode(data, "UTF-8");
		} else {
			throw new IOException("Data illegal");
		}
		JSONObject job = (JSONObject) JSONValue.parse(decodedContent);
		//test
		resp.getWriter().print(decodedContent);
		//Test NullPointerException
		String set = job.toJSONString();
		String password = RC4.encrypt( (String) job.get("password"), RC4.key());
		String username = RC4.encrypt( (String) job.get("username"),RC4.key()) ;
		// need authenticate
		resp.setContentType("text/plain");
		PrintWriter out = resp.getWriter();
		Boolean createSuccess = false;
		if (!password.isEmpty() && !username.isEmpty()) {
			createSuccess = UserAccount.createUserAccount(username, password);
		} else {
			out.print("Invalid request: no password or username");
		}
		if (createSuccess) {
			out.print("Successfully created user: " + username);
		} else {
			out.print("Failed to create user. ");
		}
	}

}
