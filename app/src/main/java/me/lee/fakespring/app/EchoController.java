package me.lee.fakespring.app;

import me.lee.fakespring.framework.beans.Autowired;
import me.lee.fakespring.framework.web.annotation.Controller;
import me.lee.fakespring.framework.web.annotation.RequestMapping;
import me.lee.fakespring.framework.web.annotation.RequestParam;

@Controller
public class EchoController {

    @Autowired
    private EchoService echoService;

    @RequestMapping("/echo")
    public String echo(@RequestParam(value = "msg", required = false, defaultValue = "null value") String msg) {
        return echoService.echo(msg);
    }

}
