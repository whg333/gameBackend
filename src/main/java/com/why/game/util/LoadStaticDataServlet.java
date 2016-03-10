package com.why.game.util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@SuppressWarnings("serial")
public class LoadStaticDataServlet extends HttpServlet {

    public void init() throws ServletException {
        String contextPath = getServletContext().getRealPath("/");
        ApplicationContext ac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        BootStrap bootStrap = new BootStrap(ac, contextPath);
        bootStrap.startServer();
    }
}
