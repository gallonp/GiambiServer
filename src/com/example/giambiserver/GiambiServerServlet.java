package com.example.giambiserver;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class GiambiServerServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, worldGi");
		
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		//sString username = req.getInputStream();
		String pwd = (String)req.getAttribute("pwd");
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world2xxx");
	}
}
