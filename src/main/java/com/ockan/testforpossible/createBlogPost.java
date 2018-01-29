package com.ockan.testforpossible;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

import com.google.appengine.api.datastore.Entity;


import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

@SuppressWarnings("serial")
//@WebServlet(name = "createBlogPost", value="/create")

@WebServlet(
	    name = "createBlogPost",
	    urlPatterns = {"/create"}
	)
public class createBlogPost extends HttpServlet {

//  @Override
//  public void doPost(HttpServletRequest req, HttpServletResponse resp)
//      throws ServletException, IOException {
//
//    PrintWriter out = resp.getWriter();
//
//    out.println(
//        "Article with the title: " + req.getParameter("title") + " by "
//            + req.getParameter("author") + " and the content: "
//            + req.getParameter("description") + " added.");
//  }
	
	DatastoreService datastore;

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {

	  // Create a map of the httpParameters that we want and run it through jSoup
	  Map<String, String> blogContent =
	      req.getParameterMap()
	          .entrySet()
	          .stream()
	          .filter(a -> a.getKey().startsWith("blogContent_"))
	          .collect(
	              Collectors.toMap(
	                  p -> p.getKey(), 
	                  p -> Jsoup.clean(p.getValue()[0], Whitelist.basic())));
	  				  //p -> p.getValue()[0]));
	  
	  Entity post = new Entity("Blogpost"); // create a new entity

	  post.setProperty("title", blogContent.get("blogContent_title"));
	  post.setProperty("author", blogContent.get("blogContent_author"));
	  post.setProperty("body", blogContent.get("blogContent_description"));
	  post.setProperty("timestamp", new Date().getTime());

	  try {
	    datastore.put(post); // store the entity

	    // Send the user to the confirmation page with personalised confirmation text
	    String confirmation = "Post with title " + blogContent.get("blogContent_title") + " created.";

	    req.setAttribute("confirmation", confirmation);
	    try {
			req.getRequestDispatcher("/confirm.jsp").forward(req, resp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  } catch (DatastoreFailureException e) {
	    throw new ServletException("Datastore error", e);
	  }
	}

	@Override
	public void init() throws ServletException {

	  // setup datastore service
	  datastore = DatastoreServiceFactory.getDatastoreService();
	}
	
}


