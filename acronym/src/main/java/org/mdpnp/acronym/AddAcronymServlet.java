package org.mdpnp.acronym;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AddAcronymServlet
 */
//@WebServlet("/AddAcronymServlet")
public class AddAcronymServlet extends AbstractAcronymServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddAcronymServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response, Connection conn) throws ServletException, IOException, SQLException {
		session.setAttribute(AcronymServlet.ALL_RESULTS, null);
		PreparedStatement ps = conn.prepareStatement("INSERT INTO acronym (acronym, meaning, URL) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		ps.setString(1, request.getParameter("acronym"));
		ps.setString(2, request.getParameter("meaning"));
		ps.setString(3, request.getParameter("url"));
		ps.execute();
		ResultSet rs = ps.getGeneratedKeys();
		ps.close();
		response.sendRedirect("acronym");
	}
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response, Connection conn)
			throws ServletException, IOException, SQLException {
		doPost(request, response, conn);
	}

}
