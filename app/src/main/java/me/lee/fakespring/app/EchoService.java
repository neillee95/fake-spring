package me.lee.fakespring.app;

import me.lee.fakespring.framework.beans.Bean;

@Bean
class EchoService {

    String echo(String msg) {
        return "You said \"" + msg + "\"";
    }

}
