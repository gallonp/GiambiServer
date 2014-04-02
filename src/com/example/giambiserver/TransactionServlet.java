package com.example.giambiserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.appengine.api.datastore.Entity;

/**
 * 
 * @author haolidu
 * 
 */
@SuppressWarnings("serial")
public class TransactionServlet extends HttpServlet {
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger
            .getLogger(TransactionServlet.class.getCanonicalName());
    /**
     * 
     */
    private static final String ID_STRING = "id";
    /**
     * 
     */
    private static final String TEXT_STRING = "text/plain";
    /**
     * 
     */
    private static final String USERNAME_STRING = "username";
    /**
     * 
     */
    private static final String ACCOUNTNUMBER_STRING = "accountNumber";
    /**
     * 
     */
    private static final String MERCHANT_STRING = "merchant";
    /**
     * 
     */
    private static final String AMOUNT_STRING = "amount";
    /**
     * 
     */
    private static final String TRNASCATIONNAME_STRING = "transactionName";
    /**
     * 
     */
    private static final String CATEGORY_STRING = "category";
    /**
     * 
     */
    private static final String CREATEDATE_STRING = "createDate";
    /**
     * 
     */
    private static final String ID_IS_EMPTY = "Invalid request: id is empty";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType(TEXT_STRING);
        PrintWriter out = resp.getWriter();

        String id = req.getParameter(ID_STRING);
        String username = req.getParameter(USERNAME_STRING);
        String accountNumber = req.getParameter(ACCOUNTNUMBER_STRING);
        LOGGER.log(Level.INFO, "id: " + id + " username: " + username
                + accountNumber);
        if (id != null) {
            if (id.isEmpty()) {
                out.print(ID_IS_EMPTY);
                LOGGER.log(Level.WARNING, ID_IS_EMPTY);
            }
            long idLong = -1;
            try {
                idLong = Long.parseLong(id);
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid request: id is not a number");
                return;
            }
            Entity transaction = Transaction.getTransaction(idLong);
            if (transaction == null) {
                LOGGER.log(Level.WARNING, "Invalid request: Transaction id: "
                        + idLong + " can not be found.");
                out.print("Invalid request: Transaction id: " + idLong
                        + " can not be found.");
                return;
            } else {
                LOGGER.log(Level.INFO,
                        "Transaction query success. Transaction id: " + idLong
                                + " returned.");
                String json = Util.writeJSON(transaction);
                out.print(json);
            }

        } else if (accountNumber != null && username != null
                && !accountNumber.isEmpty()) {
            // if (SessionCookie.verifySessionCookie(req, username)) {
            Iterable<Entity> entities = Transaction.getAccountTransactions(
                    username, accountNumber);
            out.print(Util.writeJSON(entities));
            // } else {
            // out.print("Invalid request: Timed out");
            // }
        } else if (username != null) {
            // if (SessionCookie.verifySessionCookie(req, username)) {
            Iterable<Entity> entities = Transaction
                    .getAllTransactions(username);
            out.print(Util.writeJSON(entities));
            // } else {
            // out.print("Invalid request: Timed out");
            // }
        } else {
            out.print("Invalid request: No valid parameters");
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType(TEXT_STRING);
        PrintWriter out = resp.getWriter();

        String data = req.getParameter("json");
        String decodedContent = "";
        if (data != null) {
            decodedContent = URLDecoder.decode(data, "UTF-8");
        } else {
            throw new IOException("Data illegal");
        }
        JSONObject json = (JSONObject) JSONValue.parse(decodedContent);
        String transactionName = (String) json.get(TRNASCATIONNAME_STRING);
        String username = (String) json.get(USERNAME_STRING);
        if (transactionName == null || username == null
                || transactionName.isEmpty() || username.isEmpty()) {
            out.print("Invalid request: missing credential");
            LOGGER.log(Level.WARNING, "Missing Credentials in Transaction");
            return;
        }

        // Checks for session cookie
        // if (!SessionCookie.verifySessionCookie(req, username)) {
        // out.print("Invalid request: Timed out");
        // return;
        // }

        Date updateDate = new Date();
        String amount = ((Double) json.get(AMOUNT_STRING)).toString();
        String category = (String) json.get(CATEGORY_STRING);
        String createDate = (String) json.get(CREATEDATE_STRING);
        String merchant = (String) json.get(MERCHANT_STRING);
        String accountNumber = (String) json.get(ACCOUNTNUMBER_STRING);
        long id;
        if (json.get(ID_STRING) != null) {
            id = (long) json.get(ID_STRING);
        } else {
            id = 0;
        }
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
        map.put(USERNAME_STRING, username);
        map.put(TRNASCATIONNAME_STRING, transactionName);
        map.put("updateDate", updateDate.toString());
        map.put(CREATEDATE_STRING, createDate);
        map.put(CATEGORY_STRING, category);
        map.put(MERCHANT_STRING, merchant);
        map.put(AMOUNT_STRING, amount);
        map.put(ACCOUNTNUMBER_STRING, accountNumber);
        long transactionId = -1;
        if (id != 0) {
            try {
                transactionId = id;
                map.remove(CREATEDATE_STRING);
            } catch (NumberFormatException e) {
                out.print("Invalid request: invalid id, Parse transaction id error.");
                LOGGER.log(Level.WARNING,
                        "Invalid transaction id: Parse transaction id error.");
                return;
            }
        }
        try {
            LOGGER.log(Level.INFO, "Creating transaction");
            transactionId = Transaction.createOrUpdateTransaction(
                    transactionId, map);
            LOGGER.log(Level.INFO, transactionId + "");
        } catch (IllegalBankAccountException e) {
            LOGGER.log(Level.WARNING, "Invalid bank account");
            LOGGER.log(Level.WARNING, e.getMessage());
            out.print("Invalid request: invalid bank account, transaction NOT saved.");
            return;
        }

        if (transactionId != -1) {
            LOGGER.log(Level.INFO, "transaction saved." + " id: "
                    + transactionId);
            out.print(transactionId);
        } else {
            LOGGER.log(Level.WARNING, "Transaction save FAILED.");
            out.print("Invalid request: NO parameters, Transaction save FAILED.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        super.doDelete(req, resp);
        resp.setContentType(TEXT_STRING);
        PrintWriter out = resp.getWriter();

        String data = req.getParameter("json");
        String decodedContent = "";
        if (data != null) {
            decodedContent = URLDecoder.decode(data, "UTF-8");
        } else {
            throw new IOException("Data illegal");
        }
        JSONObject json = (JSONObject) JSONValue.parse(decodedContent);
        String username = (String) json.get(USERNAME_STRING);
        long id;
        if (json.get(ID_STRING) != null) {
            id = (long) json.get(ID_STRING);
            LOGGER.log(Level.INFO, "Deleteing transaction with id: " + id);
        } else {
            id = 0;
        }
        if (username == null || id == 0 || username.isEmpty()) {
            out.print("Invalid request: missing credential");
            LOGGER.log(Level.WARNING, "Missing Credentials in deleting transactions.");
            return;
        }
        LOGGER.log(Level.INFO, Transaction.deleteTransaction(id));
        out.print("Success");
    }

}
