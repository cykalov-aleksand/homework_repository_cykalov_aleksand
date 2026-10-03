package ru.otus.appcontaine;

import ru.otus.appcontaine.api.AppComponent;
import ru.otus.appcontaine.api.AppComponentsContainer;
import ru.otus.appcontaine.api.AppComponentsContainerConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();
    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        this(new Class<?>[]{initialConfigClass});
    }

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
    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);
        try {
            Object configInstance = configClass.getDeclaredConstructor().newInstance();
            List<Method> beanMethods = Arrays.stream(configClass.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(AppComponent.class))
                    .sorted(Comparator.comparingInt(method -> method.getAnnotation(AppComponent.class).order())).toList();
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
}
