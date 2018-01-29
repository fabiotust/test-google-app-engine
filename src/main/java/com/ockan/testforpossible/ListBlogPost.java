package com.ockan.testforpossible;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

/**
 * Servlet implementation class ListBlogPost
 */
@WebServlet("/ListBlogPost")
public class ListBlogPost extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ListBlogPost() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    DatastoreService datastore;
    String blogPostDisplayFormat =
    	    "<h2> TITLE: %s </h2> Posted at: %s by %s <br><br>";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		PrintWriter out = response.getWriter();
		
		
		final Query q =
			    new Query("Blogpost").setFilter(new FilterPredicate("title", FilterOperator.NOT_EQUAL, ""));

			PreparedQuery pq = datastore.prepare(q);
			List<Entity> posts = pq.asList(FetchOptions.Builder.withLimit(50)); // Retrieve up to five posts

			posts.forEach(
			    (result) -> {
			      // Grab the key and convert it into a string in preparation for encoding
			      //String keyString = KeyFactory.keyToString(result.getKey());

			      // Encode the entity's key with Base64
			      //String encodedID = new String(Base64.getUrlEncoder().encodeToString(String.valueOf(keyString).getBytes()));

			      // Build up string with values from the Datastore entity
			      String recordOutput =
			          String.format( blogPostDisplayFormat, result.getProperty("title"), result.getProperty("timestamp"),
			              result.getProperty("author"));

			      out.println(recordOutput); // Print out HTML
			    });
			
			//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	@Override
	public void init() throws ServletException {

	  // setup datastore service
	  datastore = DatastoreServiceFactory.getDatastoreService();
	}

}
