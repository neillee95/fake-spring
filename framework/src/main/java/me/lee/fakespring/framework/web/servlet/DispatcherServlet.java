package me.lee.fakespring.framework.web.servlet;

import me.lee.fakespring.framework.web.handler.HandlerManager;
import me.lee.fakespring.framework.web.handler.MappingHandler;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class DispatcherServlet implements Servlet {

    @Override
    public void init(ServletConfig config) {
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws IOException {
        for (MappingHandler mappingHandler : HandlerManager.mappingHandlers) {
            try {
                if (mappingHandler.handle(req, res)) {
                    return;
                }
            } catch (IllegalAccessException | InvocationTargetException | IOException e) {
                e.printStackTrace();
            }
        }
        ((HttpServletResponse)res).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {
    }

}
