package me.lee.fakespring.framework.beans;

import me.lee.fakespring.framework.exception.BeanInitException;
import me.lee.fakespring.framework.web.annotation.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BeanFactory {

    private static final Logger log = LoggerFactory.getLogger(BeanFactory.class);

    private static Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();

    public static Object getBean(Class<?> cls) {
        return beanMap.get(cls);
    }

    public static void initBean(List<Class<?>> classes) throws BeanInitException {
        List<Class<?>> toInit = new ArrayList<>(classes);
        while (toInit.size() > 0) {
            int remainSize = toInit.size();
            Iterator<Class<?>> iterator = toInit.iterator();
            try {
                while (iterator.hasNext()) {
                    Class<?> cls = iterator.next();
                    if (create(cls)) {
                        iterator.remove();
                    }
                }
            }catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                e.printStackTrace();
            }
            if (remainSize == toInit.size()) {
                throw new BeanInitException("Cannot init beans: " + Arrays.toString(toInit.toArray()));
            }
        }
    }

    private static boolean create(Class<?> cls) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (!cls.isAnnotationPresent(Bean.class)
                && !cls.isAnnotationPresent(Controller.class)) {
            return true;
        }
        Constructor constructor = cls.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object instance = constructor.newInstance();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Object reliantBean = getBean(field.getType());
                if (reliantBean == null) {
                    return false;
                }
                field.setAccessible(true);
                field.set(instance, reliantBean);
            }
        }
        log.info("Init bean [{}]", cls.getName());
        beanMap.put(cls, instance);
        return true;
    }

}
