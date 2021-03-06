// Copyright 2011, Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.example.giambiserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

/**
 * This is the utility class for all servlets. It provides method for inserting,
 * deleting, searching the entity from data store. Also contains method for
 * displaying the entity in JSON format.
 * 
 */
public class Util {

    /**
     * 
     */
    private static final Logger LOGGER = Logger.getLogger(Util.class
            .getCanonicalName());
    /**
     * 
     */
    private static DatastoreService datastore = DatastoreServiceFactory
            .getDatastoreService();

    /**
     * 
     * @param entity
     *            : entity to be persisted
     */
    public static void persistEntity(Entity entity) {
        // logger.log(Level.INFO, "Saving entity");
        datastore.put(entity);
    }

    /**
     * Delete the entity from persistent store represented by the key.
     * 
     * @param key
     *            : key to delete the entity from the persistent store
     */
    public static void deleteEntity(Key key) {
        // logger.log(Level.INFO, "Deleting entity");
        datastore.delete(key);
    }

    /**
     * Delete list of entities given their keys.
     * 
     * @param keys 
     */
    public static void deleteEntity(final List<Key> keys) {
        datastore.delete(new Iterable<Key>() {
            public Iterator<Key> iterator() {
                return keys.iterator();
            }
        });
    }

    /**
     * Search and return the entity from datastore.
     * 
     * @param key
     *            : key to find the entity
     * @return entity
     */

    public static Entity findEntity(Key key) {
        // logger.log(Level.INFO, "Search the entity");
        try {
            return datastore.get(key);
        } catch (EntityNotFoundException e) {
            LOGGER.log(Level.WARNING, "Entity not found for " + key.getId());
            return null;
        }
    }

    /***
     * Search entities based on search criteria.
     * 
     * @param kind 
     * @param searchBy 
     *            : Searching Criteria (Property)
     * @param searchFor
     *            : Searching Value (Property Value)
     * @return List all entities of a kind from the cache or datastore (if not
     *         in cache) with the specified properties
     */
    public static Iterable<Entity> listEntities(String kind, String searchBy,
            String searchFor) {
        return listEntitiesFilters(kind, searchBy, searchFor);
    }

    /***
     * Search entities based on search criteria, may have multiple filters.
     * 
     * @param kind 
     * @param searchBy 
     *            : Searching Criteria (Property)
     * @param searchFor
     *            : Searching Value (Property Value)
     * @param filters
     *            additional searchBy
     * @return List all entities of a kind from the cache or datastore (if not
     *         in cache) with the specified properties
     */
    @SuppressWarnings("deprecation")
    public static Iterable<Entity> listEntitiesFilters(String kind,
            String searchBy, String searchFor, String... filters) {
        // logger.log(Level.INFO, "Search entities based on search criteria: " +
        // searchBy + " = " + searchFor + " and other filters");
        Query q = new Query(kind);
        if (searchFor != null && !"".equals(searchFor)) {
            q.addFilter(searchBy, FilterOperator.EQUAL, searchFor);
        }
        if (filters != null && filters.length >= 2 && filters.length % 2 == 0) {
            for (int i = 0; i < filters.length / 2; i++) {
                i = i * 2;
                q.addFilter(filters[i], FilterOperator.EQUAL, filters[i + 1]);
            }
        }
        PreparedQuery pq = datastore.prepare(q);
        return pq.asIterable();
    }

    /**
     * Search entities based on ancestor.
     * 
     * @param kind 
     * @param ancestor 
     * @return Iterable<Entity> 
     */
    @SuppressWarnings("deprecation")
    public static Iterable<Entity> listChildren(String kind, Key ancestor) {
        // logger.log(Level.INFO, "Search entities based on parent");
        Query q = new Query(kind);
        q.setAncestor(ancestor);
        q.addFilter(Entity.KEY_RESERVED_PROPERTY, FilterOperator.GREATER_THAN,
                ancestor);
        PreparedQuery pq = datastore.prepare(q);
        return pq.asIterable();
    }

    /**
     * 
     * @param kind 
     * @param ancestor 
     * @return Iterable<Entity> 
     */
    @SuppressWarnings("deprecation")
    public static Iterable<Entity> listChildKeys(String kind, Key ancestor) {
        LOGGER.log(Level.INFO, "Search entities based on parent");
        Query q = new Query(kind);
        q.setAncestor(ancestor).setKeysOnly();
        q.addFilter(Entity.KEY_RESERVED_PROPERTY, FilterOperator.GREATER_THAN,
                ancestor);
        PreparedQuery pq = datastore.prepare(q);
        return pq.asIterable();
    }

    /**
     * List the entities in JSON format.
     * 
     * @param entities
     *            entities to return as JSON strings
     * @return String 
     */
    public static String writeJSON(Iterable<Entity> entities) {
        LOGGER.log(Level.INFO, "creating JSON format object");
        StringBuilder sb = new StringBuilder();

        int i = 0;
        sb.append("{\"data\": [");
        for (Entity result : entities) {
            Map<String, Object> properties = result.getProperties();
            sb.append("{");
            if (result.getKey().getName() == null)
                sb.append("\"name\" : \"" + result.getKey().getId() + "\",");
            else
                sb.append("\"name\" : \"" + result.getKey().getName() + "\",");

            for (String key : properties.keySet()) {
                sb.append("\"" + key + "\" : \"" + properties.get(key) + "\",");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("},");
            i++;
        }
        if (i > 0) {
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        sb.append("]}");
        return sb.toString();
    }

    /**
     * 
     * @param entity 
     * @return String 
     */
    public static String writeJSON(Entity entity) {
        List<Entity> entities = new ArrayList<>();
        entities.add(entity);
        return writeJSON(entities);
    }

    /**
     * Retrieves Parent and Child entities into JSON String.
     * 
     * @param entities
     *            : List of parent entities
     * @param childKind
     *            : Entity type for Child
     * @param fkName
     *            : foreign-key to the parent in the child entity
     * @return JSON string
     */
    public static String writeJSON(Iterable<Entity> entities, String childKind,
            String fkName) {
        LOGGER.log(Level.INFO,
                "creating JSON format object for parent child relation");
        StringBuilder sb = new StringBuilder();
        int i = 0;
        sb.append("{\"data\": [");
        for (Entity result : entities) {
            Map<String, Object> properties = result.getProperties();
            sb.append("{");
            if (result.getKey().getName() == null)
                sb.append("\"id\" : \"" + result.getKey().getId() + "\",");
            else
                sb.append("\"id\" : \"" + result.getKey().getName() + "\",");
            for (String key : properties.keySet()) {
                sb.append("\"" + key + "\" : \"" + properties.get(key) + "\",");
            }
            Iterable<Entity> child = listEntities(childKind, fkName,
                    String.valueOf(result.getKey().getId()));
            for (Entity en : child) {
                for (String key : en.getProperties().keySet()) {
                    sb.append("\"" + key + "\" : \""
                            + en.getProperties().get(key) + "\",");
                }
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("},");
            i++;
        }
        if (i > 0) {
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        sb.append("]}");
        return sb.toString();
    }

    /**
     * Utility method to send the error back to UI.
     * 
     * @param ex
     * @throws IOException
     */
    public static String getErrorMessage(Exception ex) throws IOException {
        return "Error:" + ex.toString();
    }

    /**
     * get DatastoreService instance.
     * 
     * @return DatastoreService instance
     */
    public static DatastoreService getDatastoreServiceInstance() {
        return datastore;
    }

    /**
     * HttpContentReader.
     * @param input 
     * @return String
     */
    public static String HttpContentReader(InputStream input) {

        BufferedReader in = new BufferedReader(new InputStreamReader(input));

        String inputLine = "";
        StringBuffer response = new StringBuffer();

        try {
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        try {
            in.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return response.toString();
    }

    /**
     * 
     * @param request 
     * @return String 
     * @throws IOException 
     */
    public static String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(
                        inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }
}