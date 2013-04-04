package org.mdpnp.hittracker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement("INSERT INTO hits (remote_addr, remote_host, query_string) values (?,?,?)");
			ps.setString(1, req.getRemoteAddr());
			ps.setString(2, req.getRemoteHost());
			ps.setString(3, req.getQueryString());
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
