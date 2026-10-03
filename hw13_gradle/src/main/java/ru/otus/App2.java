package ru.otus;

import ru.otus.appcontaine.AppComponentsContainerImpl;
import ru.otus.appcontaine.api.AppComponentsContainer;
import ru.otus.services.GameProcessor;

public class App2 {
    public static void main(String[] args) {
      AppComponentsContainer container = new AppComponentsContainerImpl("ru.otus.config");
       GameProcessor gameProcessor = container.getAppComponent("gameProcessor");
        gameProcessor.startGame();
    }
}
