package com.example.giambiserver;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.logging.Level;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.appengine.api.datastore.Entity;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
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
		String password = (String) job.get("password");
		String username = (String) job.get("username");
		
		Entity user = UserAccount.getSingleUser(username);
		if (user!=null){
			String dbUsername = (String) user.getProperty("username");
			String dbPassword = (String) user.getProperty("password");
			if (username.equalsIgnoreCase(dbUsername)&&password.equalsIgnoreCase(dbPassword)){
				resp.getWriter().println("Login succeeded!");
			} else {
				resp.getWriter().println("Password doesn't match username.");
			}
		} else {
			resp.getWriter().println("Username hasn't been registered.");
		}
	}
}