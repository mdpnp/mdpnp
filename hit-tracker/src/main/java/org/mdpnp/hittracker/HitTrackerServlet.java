package org.mdpnp.hittracker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class HitTrackerServlet extends HttpServlet {
	private final byte[] bytes;
	
	@Resource(name="jdbc/mdpnp_hittracker")
	private DataSource dataSource;

	
	public HitTrackerServlet() throws IOException {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		g.setColor(new Color(1.0f, 1.0f, 1.0f, 0.0f));
		g.drawRect(0, 0, 1, 1);
		g.dispose();
		ImageIO.write(bi, "PNG",  baos);
		bytes = baos.toByteArray();
	}
	
	private final Logger LOG = LoggerFactory.getLogger(HitTrackerServlet.class);
	
	private static final int DEFAULT_LIMIT = 20;
	private static final int getLimit(HttpServletRequest req) {
		String limit = req.getParameter("limit");
		if(null == limit) {
			return DEFAULT_LIMIT;
		} else {
			try {
				return Integer.parseInt(limit);
			} catch (NumberFormatException nfe) {
				return DEFAULT_LIMIT;
			}
		}
	}
	
	
	protected static final void dump(ResultSet rs, PrintWriter pw) throws SQLException {
		ResultSetMetaData rsm = rs.getMetaData();
		int columns = rsm.getColumnCount();
		for(int i = 0; i < columns; i++) {
			pw.write(rsm.getColumnName(i+1));
			pw.write("\t");
		}
		pw.write("\n");
		
		while(rs.next()) {
			for(int i = 0; i < columns; i++) {
				String s = rs.getString(i+1);
				pw.write(null == s ? "" : s);
				pw.write("\t");
			}
			pw.write("\n");
		}
	}
	protected void doFinally(ResultSet rs, Statement s, Connection c) {
		if(null != rs) {
			try {
				rs.close();
			} catch(SQLException e) {
				LOG.error("cannot close result set", e);
			}
		}
		if(null != s) {
			try {
				s.close();
			} catch (SQLException e) {
				LOG.error("cannot close statement", e);
			}
		}
		if(null != c) {
			try {
				c.close();
			} catch (SQLException e) {
				LOG.error("cannot close connection", e);
			}
		}
	}
	
	protected void doCount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement("select date_format(DATE(tm), '%c/%e/%Y') as dt, count(distinct remote_addr) as cnt_unique from hits group by DATE(tm) order by DATE(tm) desc;");
			
			if(ps.execute()) {
				resp.setContentType("text/plain");
				resp.setStatus(200);
				rs = ps.getResultSet();
				
				PrintWriter pw = resp.getWriter();
				dump(rs, pw);
				
				pw.close();
			}
		} catch (SQLException e) {
			LOG.error("cannot select count", e);
		} finally {
			doFinally(rs, ps, conn);
		}

	}

	
	protected void doList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement("SELECT * FROM hits ORDER BY tm DESC LIMIT ?");
			
			ps.setInt(1, getLimit(req));
			
			if(ps.execute()) {
				resp.setContentType("text/plain");
				resp.setStatus(200);
				rs = ps.getResultSet();
				
				PrintWriter pw = resp.getWriter();
				dump(rs, pw);
				pw.close();
			}
		} catch (SQLException e) {
			LOG.error("cannot select list", e);
		} finally {
			doFinally(rs, ps, conn);
		}
		resp.setContentLength(bytes.length);
		resp.setContentType("image/png");
		resp.setStatus(200);
		resp.getOutputStream().write(bytes);
		resp.getOutputStream().close();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		if("/LIST".equals(req.getPathInfo())) {
			doList(req, resp);
			return;
		} else if("/COUNT".equals(req.getPathInfo())) {
			doCount(req, resp);
			return;
		}
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement("INSERT INTO hits (remote_addr, remote_host, query_string, context_path, path_info) values (?,?,?,?,?)");
			ps.setString(1, req.getRemoteAddr());
			ps.setString(2, req.getRemoteHost());
			ps.setString(3, req.getQueryString());
			ps.setString(4, req.getContextPath());
			ps.setString(5, req.getPathInfo());
			
			ps.execute();
		} catch (SQLException e) {
			LOG.error("cannot insert hit", e);
		} finally {
			if(null != ps) {
				try {
					ps.close();
				} catch (SQLException e) {
					LOG.error("cannot close statement", e);
				}
			}
			if(null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOG.error("cannot close connection", e);
				}
			}
		}
		resp.setContentLength(bytes.length);
		resp.setContentType("image/png");
		resp.setStatus(200);
		resp.getOutputStream().write(bytes);
		resp.getOutputStream().close();
	}
}
