package org;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.view.VelocityLayoutServlet;

/**
 * 自定义velocity的错误处理
 * 
 */
@SuppressWarnings("serial")
public final class VelocityServlet extends VelocityLayoutServlet {

	private final static Log LOG = LogFactory.getLog(VelocityServlet.class);

	protected void error(HttpServletRequest req, HttpServletResponse res,
			Throwable excp) {

		Throwable t = excp;
		if (excp instanceof MethodInvocationException)
			t = ((MethodInvocationException) excp).getWrappedThrowable();

		try {
			if (t instanceof ResourceNotFoundException) {
				LOG.error(t.getMessage() + "(" + req.getRequestURL().toString()
						+ ")");
				if (!res.isCommitted())
					res.sendError(HttpServletResponse.SC_NOT_FOUND);
			} else {
				StringBuilder log = new StringBuilder(
						"ERROR：Unknown Velocity Error，url=");
				log.append(req.getRequestURL());
				if (req.getQueryString() != null) {
					log.append('?');
					log.append(req.getQueryString());
				}
				log.append('(');
				log.append(new Date());
				log.append(')');
				LOG.error(log.toString(), t);
				log = null;
				req.setAttribute("javax.servlet.jsp.jspException", t);
				res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} catch (IOException e) {
			LOG.error("Exception occured in VelocityServlet.error", e);
		} catch (IllegalStateException e) {
			LOG.error("==============<<IllegalStateException>>==============",
					e.getCause());
		}
		return;
	}
}