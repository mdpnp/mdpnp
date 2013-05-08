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
 * Servlet implementation class AbstractAcronymServlet
 */
public abstract class AbstractAcronymServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /** 
     * @see HttpServlet#HttpServlet()
     */
    public AbstractAcronymServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected abstract void doGet(HttpServletRequest request, HttpServletResponse response, Connection conn) throws ServletException, IOException, SQLException;
    protected abstract void doPost(HttpServletRequest request, HttpServletResponse response, Connection conn) throws ServletException, IOException, SQLException;
    
    protected HttpSession session;
    protected Context context;
    
    protected void forward(String uri, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	getServletContext().getRequestDispatcher(uri).forward(request, response);
    }
    
    public static final String JNDI_CONTEXT = "JNDI_CONTEXT";
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		session = request.getSession();
		
		Connection c = null;
		try {
			context = (Context) session.getAttribute(JNDI_CONTEXT);
			if(null == context) {
				context = new InitialContext();
				session.setAttribute(JNDI_CONTEXT, context);
			}

			DataSource ds = (DataSource) context.lookup("jdbc/ACRONYM");
			c = ds.getConnection();
			
			doGet(request, response, c);
			
		} catch (NamingException e) {
			throw new ServletException(e);
		} catch (SQLException e) {
			throw new ServletException(e);
		} finally {
			if(c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					throw new ServletException();
				}
			}
		}
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		session = request.getSession();
		
		Connection c = null;
		try {
			context = (Context) session.getAttribute(JNDI_CONTEXT);
			if(null == context) {
				context = new InitialContext();
				session.setAttribute(JNDI_CONTEXT, context);
			}
			DataSource ds = (DataSource) context.lookup("jdbc/ACRONYM");
			c = ds.getConnection();
			
			doPost(request, response, c);
			
		} catch (NamingException e) {
			throw new ServletException(e);
		} catch (SQLException e) {
			throw new ServletException(e);
		} finally {
			if(c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					throw new ServletException();
				}
			}
		}
	}

}
