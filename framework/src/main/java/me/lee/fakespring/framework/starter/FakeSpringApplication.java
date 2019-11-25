package me.lee.fakespring.framework.starter;

import me.lee.fakespring.framework.beans.BeanFactory;
import me.lee.fakespring.framework.core.ClassScanner;
import me.lee.fakespring.framework.exception.BeanInitException;
import me.lee.fakespring.framework.exception.WebServerException;
import me.lee.fakespring.framework.web.handler.HandlerManager;
import me.lee.fakespring.framework.web.server.EmbeddedTomcatServer;
import me.lee.fakespring.framework.web.server.EmbeddedWebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class FakeSpringApplication {

    private static final Logger log = LoggerFactory.getLogger(FakeSpringApplication.class);

    public static void run(Class<?> cls, String[] args) {

        try {
            List<Class<?>> classes = ClassScanner.scanClasses(cls.getPackage().getName());
            if (log.isDebugEnabled()) {
                log.debug("Scanned classes");
                classes.forEach(clazz -> System.out.println(clazz.getName()));
            }
            BeanFactory.initBean(classes);
            HandlerManager.resolveMappingHandler(classes);
        } catch (IOException | ClassNotFoundException | BeanInitException | URISyntaxException e) {
            e.printStackTrace();
        }

        EmbeddedWebServer webServer = new EmbeddedTomcatServer(args);
        try {
            webServer.start();
        } catch (WebServerException e) {
            webServer.stop();
        }
    }

}
