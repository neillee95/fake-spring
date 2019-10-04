package me.lee.fakespring.framework.web.handler;

import me.lee.fakespring.framework.web.annotation.Controller;
import me.lee.fakespring.framework.web.annotation.RequestMapping;
import me.lee.fakespring.framework.web.annotation.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class HandlerManager {

    public static List<MappingHandler> mappingHandlers = new ArrayList<>();

    public static void resolveMappingHandler(List<Class<?>> classes) {
        if (classes == null) {
            throw new NullPointerException("Classes is null.");
        }
        for (Class<?> cls : classes) {
            if (cls.isAnnotationPresent(Controller.class)) {
                parseHandlerFromController(cls);
            }
        }
    }

    private static void parseHandlerFromController(Class<?> cls) {
        Method[] methods = cls.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = method.getDeclaredAnnotation(RequestMapping.class);
                String uri = requestMapping.value();
                List<RequestParam> paramNames = new ArrayList<>();
                Parameter[] parameters = method.getParameters();
                for (Parameter parameter : parameters) {
                    if (parameter.isAnnotationPresent(RequestParam.class)) {
                        paramNames.add(parameter.getAnnotation(RequestParam.class));
                    }
                }
                RequestParam[] args = paramNames.toArray(new RequestParam[0]);
                MappingHandler mappingHandler = new MappingHandler(uri, method, cls, args);
                mappingHandlers.add(mappingHandler);
            }
        }
    }

}
