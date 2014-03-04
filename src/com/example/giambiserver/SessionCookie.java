package com.example.giambiserver;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class SessionCookie {

	public static void createSessionCookie(String username, Cookie cookie) {
		Entity sessionCookie = getSessionCookie(username);
		if (sessionCookie == null) {
			sessionCookie = new Entity("SessionCookie");
			sessionCookie.setProperty("username", username);
		}
		sessionCookie.setProperty("cookieValue", cookie.getName()+"="+cookie.getValue());
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, cookie.getMaxAge());
		sessionCookie.setProperty("expiration", cal.getTime());
		Util.persistEntity(sessionCookie);
	}

	public static boolean varifySessionCookie (HttpServletRequest req, String username){
		String cookieValue = req.getParameter("Cookie");
		Cookie cookie = new Cookie ("auth-cookie",cookieValue);
		cookie.setMaxAge(120);
		return varifySessionCookie(username,cookie);
	}
	private static boolean varifySessionCookie(String username, Cookie cookie) {
		Entity sessionCookie = getSessionCookie(username);
		if (sessionCookie == null) {
			return false;
		} else {
			Calendar cal = Calendar.getInstance();
			String dbCookieValue = (String) sessionCookie
					.getProperty("cookieValue");
			Date expiration = (Date) sessionCookie.getProperty("expiration");
			if (dbCookieValue.equals(cookie.getValue())
					&& cal.getTime().before(expiration)) {
				//update expiration time;
				cal.add(Calendar.SECOND, cookie.getMaxAge());
				sessionCookie.setProperty("expiration", cal.getTime());
				Util.persistEntity(sessionCookie);
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * get Item with item name
	 * 
	 * @param itemName
	 *            : get itemName
	 * @return item entity
	 */
	public static Entity getSessionCookie(String username) {
		Query query = new Query("SessionCookie");
		Filter singleUserFilter = new Query.FilterPredicate("username",
				FilterOperator.EQUAL, username);
		query.setFilter(singleUserFilter);
		List<Entity> results = Util.getDatastoreServiceInstance()
				.prepare(query).asList(FetchOptions.Builder.withDefaults());
		if (!results.isEmpty()) {
			return (Entity) results.remove(0);
		}
		return null;
	}

}
