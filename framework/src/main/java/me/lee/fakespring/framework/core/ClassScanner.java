package me.lee.fakespring.framework.core;

import me.lee.fakespring.framework.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ClassScanner {

    private static final String JAR_RESOURCE_PROTOCOL = "jar";
    private static final String FILE_RESOURCE_PROTOCOL = "file";

    public static List<Class<?>> scanClasses(String packageName) throws IOException, ClassNotFoundException, URISyntaxException {
        if (StringUtil.isEmpty(packageName)) {
            throw new NullPointerException("Package name is empty.");
        }
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replaceAll("\\.", "/");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String protocol = resource.getProtocol();
            if (JAR_RESOURCE_PROTOCOL.equals(protocol)) {
                JarURLConnection connection = (JarURLConnection) resource.openConnection();
                String jarFilePath = connection.getJarFile().getName();
                classes.addAll(getClassesFromJar(jarFilePath, path));
            } else if (FILE_RESOURCE_PROTOCOL.equals(protocol)) {
                classes.addAll(getClassesFromFiles(path, resource));
            }
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

    private static List<Class<?>> getClassesFromFiles(String path, URL resource) throws URISyntaxException, IOException {
        File file = new File(resource.toURI());
        return Files.walk(file.toPath())
                .filter(it -> it.toFile().getName().endsWith(".class"))
                .map(it -> {
                    String fileName = it.toString();
                    try {
                        return Class.forName(it.toString().replaceAll("/", ".")
                                .substring(fileName.indexOf(path), fileName.indexOf(".class")));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }

}
