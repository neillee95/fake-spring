package me.lee.fakespring.framework.web.server;

import me.lee.fakespring.framework.exception.WebServerException;
import me.lee.fakespring.framework.web.servlet.DispatcherServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedTomcatServer implements EmbeddedWebServer {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedTomcatServer.class);

    private Tomcat tomcat;

    private String[] args;

    public EmbeddedTomcatServer(String[] args) {
        this.args = args;
    }

    @Override
    public void start() throws WebServerException {
        tomcat = new Tomcat();
        tomcat.setPort(8000);
        tomcat.getConnector();

        //context
        Context context = new StandardContext();
        context.setPath("");
        context.addLifecycleListener(new Tomcat.FixContextListener());

        //servlet
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        Tomcat.addServlet(context, "dispatcherServlet", dispatcherServlet)
                .setAsyncSupported(true);
        context.addServletMappingDecoded("/", "dispatcherServlet");
        tomcat.getHost().addChild(context);

        //start tomcat server
        try {
            tomcat.start();
            log.info("Tomcat server started.");
            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
            awaitAfterStart();
        } catch (Exception e) {
            log.error("Cannot start tomcat", e);
            throw new WebServerException();
        }
    }

    private void awaitAfterStart() {
        if (tomcat == null) {
            return;
        }
        Thread awaitThread = new Thread("tomcat_await_thread") {
            @Override
            public void run() {
                tomcat.getServer().await();
            }
        };
        awaitThread.setDaemon(false);
        awaitThread.start();
    }

    @Override
    public void stop() {
        if (tomcat != null) {
            try {
                tomcat.stop();
                tomcat.destroy();
                log.info("Tomcat server stopped.");
            } catch (LifecycleException ignore) {
            }
        }
    }

}
