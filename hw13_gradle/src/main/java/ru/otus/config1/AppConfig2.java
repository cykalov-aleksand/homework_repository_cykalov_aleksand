package ru.otus.config1;

import ru.otus.appcontaine.api.AppComponent;
import ru.otus.appcontaine.api.AppComponentsContainerConfig;
import ru.otus.services.*;
@AppComponentsContainerConfig(order = 2)
public class AppConfig2 {
    @AppComponent(order = 1, name = "playerService")
    public PlayerService playerService(IOService ioService) {
        return new PlayerServiceImpl(ioService);
    }

    @AppComponent(order = 2, name = "gameProcessor")
    public GameProcessor gameProcessor(
            IOService ioService, PlayerService playerService, EquationPreparer equationPreparer) {
        return new GameProcessorImpl(ioService, equationPreparer, playerService);
    }

    @SuppressWarnings("squid:S106")
    @AppComponent(order = 0, name = "ioService")
    public IOService ioService() {
        return new IOServiceStreams(System.out, System.in);
    }
}
