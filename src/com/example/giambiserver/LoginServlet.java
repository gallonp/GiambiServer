package com.example.giambiserver;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Calendar;

import javax.servlet.http.Cookie;
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
		} else {
			throw new IOException("Data illegal.");
		}
		JSONObject job = (JSONObject) JSONValue.parse(decodedContent);
		resp.getWriter().print(decodedContent);
		String password = (String) job.get("password");
		String username = (String) job.get("username");
		
		Entity user = UserAccount.getSingleUser(username);
		if (user!=null){
			String dbUsername = RC4.decrypt((String)user.getProperty("username"),RC4.key());
			String dbPassword = RC4.decrypt((String) user.getProperty("password"), RC4.key());
			if (username.equalsIgnoreCase(dbUsername)&&password.equalsIgnoreCase(dbPassword)){
				resp.getWriter().println("Login succeeded!");
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
