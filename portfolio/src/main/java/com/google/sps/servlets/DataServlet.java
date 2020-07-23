// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import java.util.Arrays;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import java.util.*;
import java.text.*;
import java.sql.Timestamp;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;  


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
Query query = new Query("Task");
System.out.println("query:"+query);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
System.out.println("d:"+datastore);
        PreparedQuery results = datastore.prepare(query);
System.out.println("r:"+results);
        List<Comment> comments = new ArrayList<>();
        
        for (Entity entity : results.asIterable()) {
            Key id = entity.getKey();
System.out.println("key:"+entity.getKey());            
            String title = (String) entity.getProperty("title");
System.out.println("id:"+id+" title:"+title);
            long timestamp = (long) entity.getProperty("timestamp");
System.out.println("timestamp:"+timestamp);
            Date date = new Date(timestamp);
            Format format = new SimpleDateFormat("dd-MM-yyy HH:mm");
            String dateAndTime =format.format(date);
System.out.println("d&t:"+dateAndTime);

            Comment comment = new Comment(id, title, dateAndTime);
            comments.add(comment);
System.out.println("cArray:"+comments);            
        }

        Gson gson = new Gson();
        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(comments));

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String comment = request.getParameter("comment");
        long timestamp = System.currentTimeMillis();

        Entity commentEntity = new Entity("Task");
        commentEntity.setProperty("title", comment);
        commentEntity.setProperty("timestamp", timestamp);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);
        
        // Redirect back to the HTML page.
        response.sendRedirect("/index.html#add-comments");
  }
}
