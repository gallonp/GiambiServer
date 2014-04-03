package com.example.giambiserver;

import com.google.appengine.api.datastore.Entity;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.JSONArray;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.Date;

public class SpendingCategoryReportServlet extends HttpServlet {
    //private enum FIELDS {Category, Amount}
    private static final java.util.logging.Logger logger = java.util.logging.Logger
            .getLogger(SpendingCategoryReportServlet.class.getCanonicalName());


    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String data = req.getParameter("json");
        String decodedContent;
        PrintWriter respWriter = resp.getWriter();
        if (data != null) {
            decodedContent = URLDecoder.decode(data, "UTF-8");
        } else {
        	respWriter.print("Data is null.");
            throw new IOException("Data is null");
        }
        JSONObject job = (JSONObject) JSONValue.parse(decodedContent);
        String userAccount = (String) job.get("userAccount");
        String startDate = (String) job.get("startDate");
        Date dStartDate = new Date(Integer.valueOf(startDate.substring(6)) - 1900,
        		Integer.valueOf(startDate.substring(0, 2)) - 1,
        			Integer.valueOf(startDate.substring(3, 5)));
        String endDate = (String) job.get("endDate");
        Date dEndDate = new Date(Integer.valueOf(endDate.substring(6)) - 1900,
        		Integer.valueOf(endDate.substring(0, 2)) - 1,
        			Integer.valueOf(endDate.substring(3, 5)));
        if (dStartDate.compareTo(dEndDate) > 0) {
        	respWriter.print("Start date is later than end date.");
        	throw new IllegalArgumentException();
        }
        logger.log(Level.INFO,userAccount);
        List<Entity> transList = Transaction.getTransactionList(userAccount);
        int listLength = transList.size();
        Set<String> transCategories = new TreeSet<>();
        Map<String, Double> categoryAmountMap = new TreeMap<>();
        JSONObject returnData = new JSONObject();
        JSONArray returnArr = new JSONArray();
        for (int i = 0; i < listLength; i++) {
        	String thisDateProperty = (String) transList.get(i).getProperty("createDate");
        	Date dThisDate = new Date(Integer.valueOf(thisDateProperty.substring(6)) - 1900,
            		Integer.valueOf(thisDateProperty.substring(0, 2)) - 1,
            			Integer.valueOf(thisDateProperty.substring(3, 5)));
            //check
            System.out.println(dThisDate);
        	if (dThisDate.compareTo(dStartDate) < 0 || dThisDate.compareTo(dEndDate) > 0) {
        		break;
        	}
            String thisProperty = (String) transList.get(i).getProperty("category");
            transCategories.add(thisProperty);
        	if (categoryAmountMap.get(thisProperty) != null) {
        		Double oldAmount = Double.valueOf(categoryAmountMap.get(thisProperty));
        		Double thisAmount = Double.valueOf((String) transList.get(i).getProperty("amount"));
        		categoryAmountMap.put(thisProperty, oldAmount + thisAmount);
        	} else {
        		categoryAmountMap.put(thisProperty, Double.valueOf((String) transList.get(i).getProperty("amount")));
        	}
        }
        /*
        for (int i = 0; i < listLength; i++) {
            String thisAmountString = (String) transList.get(i).getProperty("amount");
            String thisProperty = (String) transList.get(i).getProperty("category");
            Double thisAmount = Double.valueOf(thisAmountString);
            Double oldAmount = categoryAmountMap.get(thisProperty);
            Double newAmount = oldAmount + thisAmount;
            categoryAmountMap.put(thisProperty, newAmount);
        }
        Object[] categories = transCategories.toArray();
        
//        JSONObject returnData = new JSONObject();
//        JSONObject returnObject = new JSONObject();
//        returnData.putAll(categoryAmountMap);
//        returnObject.put("Data", returnData);
//        returnObject.writeJSONString(respWriter);
        //		JSONArray returnArray = new JSONArray();'
       // JSONObject returnObj = new JSONObject();
        logger.log(Level.INFO,categoryAmountMap.size() + "");
        for (int i = 0; i < categoryAmountMap.size(); i++) {
            String newCategory = (String) categories[i];
            String newAmount = String.valueOf(categoryAmountMap.get(categories[i]));
            JSONObject thisObj = new JSONObject();
            thisObj.put(newCategory, newAmount);
            returnArr.add(thisObj);
//			returnArray.add(returnString);
        }
        */
        for (String s : transCategories) {
        	JSONObject thisObj = new JSONObject();
        	thisObj.put(s, String.valueOf(categoryAmountMap.get(s)));
        	returnArr.add(thisObj);
        }
        returnData.put("Data", returnArr);
       // returnData.put("Data", returnObj);
        returnData.writeJSONString(respWriter);
        logger.log(Level.INFO,returnData.toJSONString());
//        returnObj.writeJSONString(respWriter);

    }
}
