package org.mdpnp.acronym;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

/**
 * Servlet implementation class AcronymServlet
 */
//@WebServlet("/acronym")
public class AcronymServlet extends AbstractAcronymServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AcronymServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    public static final String ALL_RESULTS = "ALL_RESULTS";
    public static final String RESULTS = "RESULTS";
    

    protected List<String[]> getAllResults(Connection c) throws SQLException {
    	List<String[]> allResults = (List<String[]>) session.getAttribute(ALL_RESULTS);
    	if(null == allResults) {
    		PreparedStatement ps = c.prepareStatement("SELECT id, acronym, meaning, URL FROM acronym WHERE active = 1 ORDER BY UPPER(acronym)");
    		ResultSet rs = ps.executeQuery();
    		allResults = new ArrayList<String[]>();
    		while(rs.next()) {
    			allResults.add(new String[] {Integer.toString(rs.getInt(1)), rs.getString(2), rs.getString(3), rs.getString(4)});
    		}
    		ps.close();
    		rs.close();
    		session.setAttribute(ALL_RESULTS, allResults);
    	}
    	return allResults;
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response, Connection c) throws ServletException, IOException, SQLException {
		request.setAttribute(ALL_RESULTS, getAllResults(c));
		
		String prefix = request.getParameter("x");
		
		PreparedStatement ps = c.prepareStatement("SELECT id, acronym, meaning, URL FROM acronym WHERE active = 1 AND acronym LIKE concat('%', ?, '%') ORDER BY UPPER(acronym) ");
		
		
			
		ps.setString(1, prefix==null?"":prefix);
		ResultSet rs = ps.executeQuery();
		
		List<String[]> results = new ArrayList<String[]>();
		while(rs.next()) {
			results.add(new String[] {Integer.toString(rs.getInt(1)), rs.getString(2), rs.getString(3), rs.getString(4)});
		}
		ps.close();
		rs.close();
		request.setAttribute(RESULTS, results);
		
		forward("/acronym.jsp", request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response, Connection conn)
			throws ServletException, IOException, SQLException {
		doGet(request, response, conn);
	}

}
