package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/** Servlet that returns a random quote. */
@WebServlet("/random-quote")
public final class RandomFunFact extends HttpServlet {

  private List<String> quotes;

  @Override
  public void init() {
    quotes = new ArrayList<>();
    quotes.add("Jennifer Almost Didn't Return For The Final Season");
    quotes.add("The Cast Took A Trip To Vegas Before The Premiere");
    quotes.add("They Wanted Courteney Cox To Play Rachel");
    quotes.add("The Writers Got Creative To Cut Costs");
    quotes.add("Gunther Was Actually A Barista");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String quote = quotes.get((int) (Math.random() * quotes.size()));

    response.setContentType("text/html;");
    response.getWriter().println(quote);
  }
}