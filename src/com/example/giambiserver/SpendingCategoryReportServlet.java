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
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;

public class SpendingCategoryReportServlet extends HttpServlet {
    //private enum FIELDS {Category, Amount}
    private static final java.util.logging.Logger logger = java.util.logging.Logger
            .getLogger(SpendingCategoryReportServlet.class.getCanonicalName());


    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String data = req.getParameter("json");
        String decodedContent;
        if (data != null) {
            decodedContent = URLDecoder.decode(data, "UTF-8");
        } else {
            throw new IOException("Data is null");
        }
        JSONObject job = (JSONObject) JSONValue.parse(decodedContent);
        PrintWriter respWriter = resp.getWriter();
        String userAccount = (String) job.get("userAccount");
        logger.log(Level.INFO,userAccount);
//        List<Entity> transList = Transaction.getTransactionList(userAccount);
        List<Entity> transList = Transaction.getTransactionList(userAccount);
        int listLength = transList.size();
        TreeSet<String> transCategories = new TreeSet<>();
        TreeMap<String, Double> categoryAmountMap = new TreeMap<>();
        for (int i = 0; i < listLength; i++) {
            String thisProperty = (String) transList.get(i).getProperty("category");
            transCategories.add(thisProperty);
            categoryAmountMap.put(thisProperty, 0.0);
        }
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
        JSONObject returnData = new JSONObject();
        JSONArray returnArr = new JSONArray();
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
        returnData.put("Data", returnArr);
       // returnData.put("Data", returnObj);
        returnData.writeJSONString(respWriter);
        logger.log(Level.INFO,returnData.toJSONString());
//        returnObj.writeJSONString(respWriter);

    }
}
