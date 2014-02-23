package com.example.giambiserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.appengine.api.datastore.Entity;

@SuppressWarnings("serial")
public class RegisterServlet extends HttpServlet {
	private static final Logger logger = Logger
			.getLogger(RegisterServlet.class.getCanonicalName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		}
		
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String data = req.getParameter("json");
		String decodedContent = "";
		if (data != null) {
			decodedContent = URLDecoder.decode(data, "UTF-8");
		} else {
			System.out.print("Data is null");
		}
		JSONObject job = (JSONObject) JSONValue.parse(decodedContent);
		resp.getWriter().print(decodedContent);
		String set = job.toJSONString();
		String password = (String) job.get("password");
		String username = (String) job.get("username");
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
