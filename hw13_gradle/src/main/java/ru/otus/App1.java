package ru.otus;

import ru.otus.appcontaine.AppComponentsContainerImpl;
import ru.otus.appcontaine.api.AppComponentsContainer;
import ru.otus.congig.AppConfig1;
import ru.otus.congig.AppConfig2;
import ru.otus.services.GameProcessor;

public class App1 {
    public static void main(String[] args) {
        // Опциональные варианты
         AppComponentsContainer container = new AppComponentsContainerImpl(AppConfig1.class, AppConfig2.class);

        // Тут можно использовать библиотеку Reflections (см. зависимости)
        // AppComponentsContainer container = new AppComponentsContainerImpl("ru.otus.config");

        // Обязательный вариант
        //AppComponentsContainer container = new AppComponentsContainerImpl(AppConfig.class);

        // Приложение должно работать в каждом из указанных ниже вариантов
        GameProcessor gameProcessor = container.getAppComponent(GameProcessor.class);
        // GameProcessor gameProcessor = container.getAppComponent(GameProcessorImpl.class);
        // GameProcessor gameProcessor = container.getAppComponent("gameProcessor");
        gameProcessor.startGame();
    }
}
