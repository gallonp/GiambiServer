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
        
//		Entity user = UserAccount.getSingleUser("gallonpig");
//		String usr = (String) user.getProperty("username");
//		String pwd = (String) user.getProperty("password");
		
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		// Need parse safty check!!!!!
		String data = req.getParameter("json");
		String decodedContent = "";
		if (data != null) {
			decodedContent = URLDecoder.decode(data, "UTF-8");
		}
		// String content = Util.getBody(req);
		JSONObject job = (JSONObject) JSONValue.parse(decodedContent);
		//test
		resp.getWriter().print(decodedContent);
		//nullpointer
		String set = job.toJSONString();
		String password = (String) job.get("password");
		String username = (String) job.get("username");
		//
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
