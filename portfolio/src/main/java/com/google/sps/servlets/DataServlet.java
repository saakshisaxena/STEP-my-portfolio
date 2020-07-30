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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory; 
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    private final int defaultMaxComments = 15;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
        int max_comments;
        if (request.getParameter("maxComments") == null) {
            response.sendError(response.SC_BAD_REQUEST, "maxComment parameter missing");
            max_comments = defaultMaxComments;
        }

        try{
            max_comments = Integer.parseInt(request.getParameter("maxComments"));
        } catch(NumberFormatException e) {
            response.sendError(response.SC_BAD_REQUEST, "Value entered in \'Set maximum number of comment\' is not a number!");
            max_comments = defaultMaxComments;
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        ImmutableList<Entity> results = ImmutableList.copyOf(datastore.prepare(query).asList(FetchOptions.Builder.withLimit(max_comments))); 

        List<Comment> comments = new ArrayList<>();

        for (Entity entity : results) {
            long id = entity.getKey().getId();
            String title = (String) entity.getProperty("title");
            long timestamp = (long) entity.getProperty("timestamp");
            Comment comment = new Comment(id, title, timestamp);
            comments.add(comment);
        }

        Gson gson = new Gson();
        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(comments));
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Entity commentEntity = new Entity("Comment");

        if (request.getParameter("comment") == null)
            response.sendError(response.SC_BAD_REQUEST, "Comment parameter missing");

        commentEntity.setProperty("title", request.getParameter("comment"));
        commentEntity.setProperty("timestamp", System.currentTimeMillis());

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);
        response.sendRedirect("/index.html#add-comments");
    }
}
