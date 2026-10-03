package ru.otus.congig;

import ru.otus.appcontaine.api.AppComponent;
import ru.otus.appcontaine.api.AppComponentsContainerConfig;
import ru.otus.services.EquationPreparer;
import ru.otus.services.EquationPreparerImpl;

   @AppComponentsContainerConfig(order = 1)
    public class AppConfig1 {

        @AppComponent(order = 0, name = "equationPreparer")
        public EquationPreparer equationPreparer() {
            return new EquationPreparerImpl();
        }

    }
