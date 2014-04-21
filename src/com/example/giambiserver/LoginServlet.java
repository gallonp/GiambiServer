package com.example.giambiserver;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.appengine.api.datastore.Entity;


@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
        private static final Logger logger = Logger.getLogger(BankAccount.class
            .getCanonicalName());
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
			throw new IOException("Data illegal.");
		}
		JSONObject job = (JSONObject) JSONValue.parse(decodedContent);
		resp.getWriter().print(decodedContent);
		String password = (String) job.get("password");
		String username = (String) job.get("username");
		password = RC4.encrypt(password, RC4.key());
		username = RC4.encrypt(username, RC4.key());
		logger.log(Level.WARNING, "Username!!!!!!----!!!!: " + RC4.decrypt(username, RC4.key()));
		
		Entity user = UserAccount.getSingleUser(username);
		if (user!=null){
			String dbUsername = (String)user.getProperty("username");
			logger.log(Level.WARNING, "Bank account: " + dbUsername
		                + " not found. Balance NOT updated.");
			String dbPassword = (String) user.getProperty("password");
			if (username.equalsIgnoreCase(dbUsername)&&password.equalsIgnoreCase(dbPassword)){
			        
				resp.getWriter().println("Login succeeded! ");
				
				Calendar cal = Calendar.getInstance();
				String userAndTime = username +","+ cal.getTime().toString();
				Cookie cookie = new Cookie("auth-cookie", Integer.toString(userAndTime.hashCode()));
				cookie.setMaxAge(120);
				SessionCookie.createSessionCookie(username, cookie);
				resp.addCookie(cookie);
			} else {
				resp.getWriter().println("Password doesn't match username.");
			}
		} else {
			resp.getWriter().println("Username hasn't been registered.");
		}
	}
}
