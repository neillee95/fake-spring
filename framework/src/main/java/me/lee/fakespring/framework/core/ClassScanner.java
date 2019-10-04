package me.lee.fakespring.framework.core;

import me.lee.fakespring.framework.util.StringUtil;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassScanner {

    public static List<Class<?>> scanClasses(String packageName) throws IOException, ClassNotFoundException {
        if (StringUtil.isEmpty(packageName)) {
            throw new NullPointerException("Package name is empty.");
        }
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replaceAll("\\.", "/");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().contains("jar")) {
                JarURLConnection connection = (JarURLConnection) resource.openConnection();
                String jarFilePath = connection.getJarFile().getName();
                classes.addAll(getClassesFromJar(jarFilePath, path));
            }
//            } else {
//                //else
//            }
        }
        return classes;
    }

    private static List<Class<?>> getClassesFromJar(String jarFilePath, String path) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        JarFile jarFile = new JarFile(jarFilePath);
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String entryName = jarEntry.getName();
            if (entryName.startsWith(path) && entryName.endsWith(".class")) {
                String className = entryName.replaceAll("/", ".")
                        .substring(0, entryName.indexOf(".class"));
                classes.add(Class.forName(className));
            }
        }
        return classes;
    }

}
