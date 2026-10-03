package ru.otus.appcontaine;

import ru.otus.appcontaine.api.AppComponent;
import ru.otus.appcontaine.api.AppComponentsContainer;
import ru.otus.appcontaine.api.AppComponentsContainerConfig;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?>... initialConfigClasses) {
        if (initialConfigClasses == null || initialConfigClasses.length == 0) {
            throw new IllegalArgumentException("Не передано ни одного конфигурационного класса");
        }
        List<Class<?>> classes = Arrays.stream(initialConfigClasses)
                .filter(Objects::nonNull)
                .toList();
        classes.forEach(this::checkConfigClass);
        classes.stream()
                .sorted(Comparator.comparingInt(
                        c -> c.getAnnotation(AppComponentsContainerConfig.class).order()))
                .forEach(this::processConfig);
    }

    public AppComponentsContainerImpl(String pathDirectory) {
        this(loadConfigClassesFromPackage(pathDirectory));
    }

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        this(new Class<?>[]{initialConfigClass});
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        return componentClass.cast(findBeanByType(componentClass));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> C getAppComponent(String componentName) {
        Object bean = appComponentsByName.get(componentName);
        if (bean == null) {
            throw new RuntimeException("Не найдено ни одного bean с именем: " + componentName);
        }
        return (C) bean;
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);
        try {
            Object configInstance = configClass.getDeclaredConstructor().newInstance();
            List<Method> beanMethods = Arrays.stream(configClass.getDeclaredMethods()).filter(method -> method
                    .isAnnotationPresent(AppComponent.class)).sorted(Comparator.comparingInt(method -> method
                    .getAnnotation(AppComponent.class).order())).toList();
            for (Method method : beanMethods) {
                method.setAccessible(true);
                Object[] args = Arrays.stream(method.getParameterTypes()).map(this::findBeanByType).toArray();
                AppComponent annotation = method.getAnnotation(AppComponent.class);
                String name = annotation.name();
                if (appComponentsByName.containsKey(name)) {
                    throw new IllegalArgumentException("Дублирующее имя bean-компонента: " + name);
                }
                Object bean = method.invoke(configInstance, args);
                if (bean == null) {
                    throw new RuntimeException("Метод " + method.getName() + " вернул null");
                }
                appComponents.add(bean);
                appComponentsByName.put(name, bean);
            }
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            throw new RuntimeException(cause == null ? e : cause);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private Object findBeanByType(Class<?> type) {
        List<Object> found = appComponents.stream().filter(type::isInstance).toList();
        if (found.isEmpty()) {
            throw new RuntimeException("Не найдено ни одного bean- компонента для типа: " + type.getName());
        }
        if (found.size() > 1) {
            throw new RuntimeException("Для данного типа найдено несколько бинов: " + type.getName());
        }
        return found.get(0);
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    private static Class<?>[] loadConfigClassesFromPackage(String packageName) {
        ClassLoader classLoader = AppComponentsContainerImpl.class.getClassLoader();
        String packagePath = packageName.replace('.', '/');
        List<Class<?>> configs = new ArrayList<>();
        boolean packageFound = false;
        try {
            Enumeration<URL> resources = classLoader.getResources(packagePath);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (!"file".equals(resource.getProtocol())) {
                    continue;
                }
                packageFound = true;
                Path root = Path.of(resource.toURI());
                try (Stream<Path> files = Files.walk(root)) {
                    List<Path> classFiles = files
                            .filter(p -> p.toString().endsWith(".class"))
                            .toList();
                    for (Path file : classFiles) {
                        Path relative = root.relativize(file);
                        String binaryName = packageName + '.' + relative.toString()
                                .replace('\\', '.').replace('/', '.')
                                .replace(".class", "");
                        try {
                            Class<?> clazz = Class.forName(binaryName, false, classLoader);
                            if (clazz.isAnnotationPresent(AppComponentsContainerConfig.class)) {
                                configs.add(clazz);
                            }
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException("Не удалось загрузить класс: " + binaryName, e);
                        }
                    }
                }
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        if (!packageFound) {
            throw new IllegalArgumentException("Пакет не найден: " + packageName);
        }
        return configs.toArray(new Class<?>[0]);
    }
}
