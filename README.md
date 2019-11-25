# Fake Spring

基于内嵌的Tomcat实现(假的)Spring框架

在Spring Boot中通常执行SpringApplication类方法run()启动Spring应用
```java
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```
fake-spring使用了类似的方法
```java
import me.lee.fakespring.framework.starter.FakeSpringApplication;

public class Application {

    public static void main(String[] args) {
        FakeSpringApplication.run(Application.class, args);
    }

}
```
创建一个Controller, 为Controller添加@Controller注解
```java
import me.lee.fakespring.framework.web.annotation.Controller;

@Controller
public class EchoController {
}
```
创建一个用于返回消息的Service Bean, 为Service添加@Bean注解
```java
@Bean
class EchoService {
    
    String echo(String msg) {
        return "You said \"" + msg + "\"";
    }

}
```
在Controller中添加一个用于处理echo请求的RequestMapping
```java
import me.lee.fakespring.framework.web.annotation.Controller;
import me.lee.fakespring.framework.web.annotation.RequestMapping;
import me.lee.fakespring.framework.web.annotation.RequestParam;

@Controller
public class EchoController {

    @RequestMapping("/echo")
    public String echo(@RequestParam(value = "msg", required = false, defaultValue = "nothing") String msg) {
    }

}
```
在Controller中注入Service
```java
import me.lee.fakespring.framework.web.annotation.Controller;
import me.lee.fakespring.framework.web.annotation.RequestMapping;
import me.lee.fakespring.framework.web.annotation.RequestParam;
import me.lee.fakespring.framework.beans.Autowired;

@Controller
public class EchoController {

    @Autowired
    private EchoService echoService;

    @RequestMapping("/echo")
    public String echo(@RequestParam(value = "msg", required = false, defaultValue = "nothing") String msg) {
        return echoService.echo(msg);
    }

}
```
构建、运行app
```shell script
./gradlew clean build
java -jar app/build/libs/app-1.0-SNAPSHOT.jar
```
请求echo
```shell script
curl "127.0.0.1:8000/echo?msg=hello"

You said "hello"
```

Just for fun.