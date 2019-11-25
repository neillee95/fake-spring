package me.lee.fakespring.framework.web.handler;

import me.lee.fakespring.framework.beans.BeanFactory;
import me.lee.fakespring.framework.web.annotation.RequestParam;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MappingHandler {

    private String uri;

    private Method method;

    private Class<?> controller;

    private RequestParam[] args;

    MappingHandler(String uri, Method method, Class<?> controller, RequestParam[] args) {
        this.uri = uri;
        this.method = method;
        this.controller = controller;
        this.args = args;
    }

    public boolean handle(ServletRequest req, ServletResponse resp)
            throws IllegalAccessException, InvocationTargetException, IOException {
        String requestUri = ((HttpServletRequest) req).getRequestURI();
        if (requestUri.equals(uri)) {
            Object[] params = new Object[args.length];
            for (int i = 0, len = args.length; i < len; i++) {
                Object param = req.getParameter(args[i].value());
                if (param == null) {
                    if (args[i].required()) {
                        ((HttpServletResponse) resp).sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad request.");
                        return true;
                    } else {
                        params[i] = args[i].defaultValue();
                    }
                } else {
                    params[i] = param;
                }
            }
            Object ctl = BeanFactory.getBean(controller);
            Object result = method.invoke(ctl, params);
            resp.getWriter().write(String.valueOf(result));
            return true;
        }
        return false;
    }

}
