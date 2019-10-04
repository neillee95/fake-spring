package me.lee.fakespring.app;

import me.lee.fakespring.framework.beans.Bean;

@Bean
class TestService {

    String echo(String msg) {
        return "handle ~ " + msg;
    }

}
