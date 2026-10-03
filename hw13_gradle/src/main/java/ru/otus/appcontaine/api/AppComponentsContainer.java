package ru.otus.appcontaine.api;

public interface AppComponentsContainer {
    <C> C getAppComponent(Class<C> componentClass);

    <C> C getAppComponent(String componentName);
}
