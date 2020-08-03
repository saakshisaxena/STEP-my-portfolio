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
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private final int defaultMaxComments = 15;
  private final String defaultLanguageCode = "en";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ImmutableList<Entity> results =
        ImmutableList.copyOf(
            datastore
                .prepare(query)
                .asList(FetchOptions.Builder.withLimit(getMaxComments(request, response))));

    List<Comment> comments = new ArrayList<>();

    for (Entity entity : results) {
      long id = entity.getKey().getId();
      String message =
          getTranslatedComment((String) entity.getProperty("message"), getLanguageCode(request));
      long timestamp = (long) entity.getProperty("timestamp");
      Comment comment = new Comment(id, message, timestamp);
      comments.add(comment);
    }

    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(gson.toJson(comments));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (request.getParameter("comment") != null) {
      Entity commentEntity = new Entity("Comment");
      commentEntity.setProperty("message", request.getParameter("comment"));
      commentEntity.setProperty("timestamp", System.currentTimeMillis());

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);
      response.sendRedirect("/index.html#add-comments");
    } else response.sendError(response.SC_BAD_REQUEST, "Comment parameter missing");
  }

  private int getMaxComments(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    int max_comments = defaultMaxComments;

    /**
     * As I would be sending maxComents and languageCode parameters sometimes together sometimes
     * separately, so even if the maxComments parameter is null, display default value of comments.
     * And when the user wants to set maxComments then by using the form and js sepcs it will give
     * the custom value.
     */
    if (request.getParameter("maxComments") != null
        && !request.getParameter("maxComments").equals("")) {
      try {
        max_comments = Integer.parseInt(request.getParameter("maxComments"));
      } catch (NumberFormatException e) {
        response.sendError(
            response.SC_BAD_REQUEST,
            "Value entered in \'Set maximum number of comment\' is not a number!");
      }
    }
    return max_comments;
  }

  private String getLanguageCode(HttpServletRequest request) {
    String languageCode = defaultLanguageCode;
    if (request.getParameter("languageCode") != null
        && request.getParameter("languageCode") != "") {
      languageCode = request.getParameter("languageCode");
    }
    return languageCode;
  }

  private String getTranslatedComment(String originalText, String languageCode) {
    // Doing the translation.
    Translate translate = TranslateOptions.getDefaultInstance().getService();
    Translation translation =
        translate.translate(originalText, Translate.TranslateOption.targetLanguage(languageCode));
    String translatedText = translation.getTranslatedText();
    return translatedText;
  }
}
