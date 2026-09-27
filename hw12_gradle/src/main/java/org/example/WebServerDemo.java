package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.cachehw.HwCache;
import org.example.cachehw.MyCache;
import org.example.core.repository.DataTemplateHibernate;
import org.example.core.repository.HibernateUtils;
import org.example.core.sesionmanager.TransactionManagerHibernate;
import org.example.crm.dbmigrations.MigrationsExecutorFlyway;
import org.example.crm.model.Address;
import org.example.crm.model.Client;
import org.example.crm.model.Phone;
import org.example.dao.DbServiceClientCache;
import org.example.server.UsersWebServer;
import org.example.server.UsersWebServerSimple;
import org.example.services.TemplateProcessor;
import org.example.services.TemplateProcessorImpl;
import org.hibernate.cfg.Configuration;

public class WebServerDemo {
        private static final int WEB_SERVER_PORT = 8081;
        private static final String TEMPLATES_DIR = "/templates/";
    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

        public static void main(String[] args) throws Exception {
            var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);
            HwCache<Long, Client> cache = new MyCache<>();
            var dbUrl = configuration.getProperty("hibernate.connection.url");
            var dbUserName = configuration.getProperty("hibernate.connection.username");
            var dbPassword = configuration.getProperty("hibernate.connection.password");

            new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

            var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

            var transactionManager = new TransactionManagerHibernate(sessionFactory);
            var clientTemplate = new DataTemplateHibernate<>(Client.class);
            var dbServiceClient = new DbServiceClientCache(transactionManager, clientTemplate, cache);
            Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);
            UsersWebServer usersWebServer = new UsersWebServerSimple(WEB_SERVER_PORT, dbServiceClient, gson, templateProcessor);
            usersWebServer.start();
            usersWebServer.join();
        }
    }

