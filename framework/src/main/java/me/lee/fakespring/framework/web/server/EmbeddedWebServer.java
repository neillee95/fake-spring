package me.lee.fakespring.framework.web.server;

import me.lee.fakespring.framework.exception.WebServerException;

public interface EmbeddedWebServer {

    void start() throws WebServerException;

    void stop();

}
