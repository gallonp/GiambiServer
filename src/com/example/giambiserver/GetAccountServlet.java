package com.example.giambiserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.appengine.api.datastore.Entity;

/**
 * @version 1.1 Last authored 2/28/14
 * @author cwl
 */
public class GetAccountServlet extends HttpServlet {

    private static final long serialVersionUID = 6083636932921870764L;
    private static final String[] FIELDS = {"bankAccountName", "bankAccountNumber",
                                            "userAccount", "bankName", "balance"};

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
     // TODO
    }

    @SuppressWarnings("unchecked")
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        
        String data = req.getParameter("json");
        String decodedContent = "";
        if (data != null) {
            decodedContent = URLDecoder.decode(data, "UTF-8");
        } else {
            throw new IOException("Data is null.");
        }
        // String content = Util.getBody(req);
        JSONObject job = (JSONObject) JSONValue.parse(decodedContent);
        //test
        PrintWriter respWriter = resp.getWriter();
//        resp.getWriter().print(decodedContent);
        //nullpointer
        String userAccount = (String) job.get("userAccount");
//        boolean doNext;
//        if (SessionCookie.varifySessionCookie(req, userAccount)) {
//			respWriter.println("Cookie varified.");
//			doNext = true;
//		} else /*if sessionCookie != null yet not varified*/ {
//				respWriter.println("Your session expired.");
//				doNext = false;
//		}
 //       if (true) {
        	List<Entity> accounts = BankAccount.getBankAccountList(userAccount);
            Integer accountNum = accounts.size();
//            JSONObject jsonObj = new JSONObject();
            JSONArray jsonArr = new JSONArray();
//            String[] temp = new String[accountNum];
            if (accountNum != 0) {
                for (int i = 0; i < accountNum; ++i) {
                    jsonArr.add(accounts.get(i).getProperties());
                }
                jsonArr.writeJSONString(respWriter);
            } else {
                respWriter.println("No accounts.");
            }
        }
    }
