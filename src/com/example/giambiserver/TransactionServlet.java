package com.example.giambiserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.appengine.api.datastore.Entity;

@SuppressWarnings("serial")
public class TransactionServlet extends HttpServlet {
    private static final Logger logger = Logger
            .getLogger(TransactionServlet.class.getCanonicalName());

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();

        String id = req.getParameter("id");
        String username = req.getParameter("username");
        String accountNumber = req.getParameter("accountNumber");
        logger.log(Level.INFO, "id: " + id + " username: "+ username + accountNumber);
        if (id != null) {
            if (id.isEmpty()) {
                out.print("Invalid request: id is empty");
                logger.log(Level.WARNING, "Invalid request: id is empty");
            }
            long idLong = 0;
            try {
                idLong = Long.parseLong(id);
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Invalid request: id is not a number");
                return;
            }
            Entity transaction = Transaction.getTransaction(idLong);
            if (transaction == null) {
                logger.log(Level.WARNING, "Invalid request: Transaction id: "
                        + idLong + " can not be found.");
                out.print("Invalid request: Transaction id: " + idLong
                        + " can not be found.");
                return;
            } else {
                logger.log(Level.INFO,
                        "Transaction query success. Transaction id: " + idLong
                                + " returned.");
                String json = Util.writeJSON(transaction);
                out.print(json);
            }

        } else if (accountNumber != null && username != null
                && !accountNumber.isEmpty()) {
            if (SessionCookie.verifySessionCookie(req, username)) {
                Iterable<Entity> entities = Transaction
                        .getAccountTransactions(username, accountNumber);
                out.print(Util.writeJSON(entities));
            } else {
                out.print("Invalid request: Timed out");
            }
        } else if (username != null) {
           // if (SessionCookie.verifySessionCookie(req, username)) {
                Iterable<Entity> entities = Transaction
                        .getAllUserTransactions(username);
                out.print(Util.writeJSON(entities));
            //} else {
            //    out.print("Invalid request: Timed out");
            //}
        } else {
            out.print("Invalid request: No valid parameters");
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();

        String data = req.getParameter("json");
        String decodedContent = "";
        if (data != null) {
            decodedContent = URLDecoder.decode(data, "UTF-8");
        } else {
            throw new IOException("Data illegal");
        }
        JSONObject json = (JSONObject) JSONValue.parse(decodedContent);
        String transactionName = (String) json.get("transactionName");
        String username = (String) json.get("username");
        if (transactionName == null || username == null
                || transactionName.isEmpty() || username.isEmpty()) {
            out.print("Invalid request: missing credential");
            logger.log(Level.WARNING, "Missing Credentials in Transaction");
            return;
        }
        
        // Checks for session cookie
        if (!SessionCookie.verifySessionCookie(req, username)) {
            out.print("Invalid request: Timed out");
            return;
        }

        Date updateDate = new Date();
        String amount = ((Double) json.get("amount")).toString();
        String category = (String) json.get("category");
        String createDate = (String) json.get("createDate");
        String merchant = (String) json.get("merchant");
        String accountNumber = (String) json.get("accountNumber");
        String id = (String) json.get("id");
        if (amount == null || amount.isEmpty()) {
            amount = "0";
        }
        if (createDate == null || createDate.isEmpty()) {
            createDate = updateDate.toString();
        }
        if (accountNumber == null || accountNumber.isEmpty()) {
            accountNumber = "cash";
        }

        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("transactionName", transactionName);
        map.put("updateDate", updateDate.toString());
        map.put("createDate", createDate);
        map.put("category", category);
        map.put("merchant", merchant);
        map.put("amount", amount);
        map.put("accountNumber", accountNumber);
        long transactionId = 0;
        if (id != null && !id.isEmpty()) {
            try {
                transactionId = Long.parseLong(id);
                map.remove("createDate");
            } catch (NumberFormatException e) {
                out.print("Invalid request: invalid id, Parse transaction id error.");
                logger.log(Level.WARNING,
                        "Invalid transaction id: Parse transaction id error.");
                return;
            }
        }
        
        try {
            logger.log(Level.INFO, "Creating transaction");
            transactionId = Transaction.createOrUpdateTransaction(
                    transactionId, map);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Invalid bank account");
            out.print("Invalid request: invalid bank account, transaction NOT saved.");
            return;
        }
        
        if (transactionId != 0) {
            logger.log(Level.INFO, "transaction saved." + " id: "
                    + transactionId);
            out.print(transactionId);
        } else {
            logger.log(Level.WARNING, "Transaction save FAILED.");
            out.print("Invalid request: NO parameters, Transaction save FAILED.");
        }
    }
}
