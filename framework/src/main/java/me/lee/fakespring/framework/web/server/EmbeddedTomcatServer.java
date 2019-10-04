package me.lee.fakespring.framework.web.server;

import me.lee.fakespring.framework.web.servlet.DispatcherServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedTomcatServer {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedTomcatServer.class);

    private Tomcat tomcat;

    private String[] args;

    public EmbeddedTomcatServer(String[] args) {
        this.args = args;
    }

    public void start() throws LifecycleException {
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
        tomcat.start();
        log.info("Tomcat server started.");
        Runtime.getRuntime().addShutdownHook(new Thread(null, () -> {
            try {
                stop();
            } catch (LifecycleException e) {
                e.printStackTrace();
            }
        }, "tomcat_shutdown_hook"));

        Thread awaitThread = new Thread(null,
                () -> tomcat.getServer().await(),
                "tomcat_await_thread");
        awaitThread.setDaemon(false);
        awaitThread.start();
    }

    private void stop() throws LifecycleException {
        if (tomcat != null) {
            tomcat.stop();
            log.info("Tomcat server stopped.");
        }
    }

}
