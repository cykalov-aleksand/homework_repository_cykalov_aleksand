package ru.otus;

import ru.otus.appcontaine.AppComponentsContainerImpl;
import ru.otus.appcontaine.api.AppComponentsContainer;
import ru.otus.config1.AppConfig1;
import ru.otus.config1.AppConfig2;
import ru.otus.services.GameProcessor;

public class App1 {
    public static void main(String[] args) {
        AppComponentsContainer container = new AppComponentsContainerImpl(AppConfig1.class, AppConfig2.class);
        GameProcessor gameProcessor = container.getAppComponent("gameProcessor");
        gameProcessor.startGame();
    }
}
