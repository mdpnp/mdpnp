package org.mdpnp.acronym;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteAcronymServlet extends AbstractAcronymServlet {

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response, Connection conn)
			throws ServletException, IOException, SQLException {
		session.setAttribute(AcronymServlet.ALL_RESULTS, null);
		PreparedStatement ps = conn.prepareStatement("UPDATE acronym SET active = 0 WHERE id=?");
		ps.setInt(1, Integer.parseInt(request.getParameter("id")));
		ps.execute();
		ps.close();
		response.sendRedirect("acronym");
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response, Connection conn)
			throws ServletException, IOException, SQLException {
		doGet(request, response, conn);
	}

}
