package org.example.server;

import com.google.gson.Gson;
import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.example.dao.DbServiceClientCache;
import org.example.helpers.FileSystemHelper;
import org.example.services.TemplateProcessor;
import org.example.servlet.AuthFilter;
import org.example.servlet.html.*;
import org.example.servlet.json.SaveUserNewClientApiServlet;
import org.example.servlet.json.UsersApiServlet;
import org.example.servlet.json.UserApiServletId;

import java.util.EnumSet;

public class UsersWebServerSimple implements UsersWebServer {
        private static final String START_PAGE_NAME = "index.html";
        private static final String COMMON_RESOURCES_DIR = "static";

       // private final UserDao userDao;
        private final Gson gson;
        protected final TemplateProcessor templateProcessor;
        private final Server server;
        private final DbServiceClientCache dbServiceClientCache;

        public UsersWebServerSimple(int port, DbServiceClientCache dbServiceClientCache, Gson gson, TemplateProcessor templateProcessor) {
            this.dbServiceClientCache = dbServiceClientCache;
            this.gson = gson;
            this.templateProcessor = templateProcessor;
            server = new Server(port);
            }

        @Override
        public void start() throws Exception {
            if (server.getHandlers().isEmpty()) {
                initContext();
            }
            server.start();
        }

        @Override
        public void join() throws Exception {
            server.join();
        }

        @Override
        public void stop() throws Exception {
            server.stop();
        }

        private void initContext() {

            ResourceHandler resourceHandler = createResourceHandler();
            ServletContextHandler servletContextHandler = createServletContextHandler();

            Handler.Sequence sequence = new Handler.Sequence();
            sequence.addHandler(resourceHandler);
            sequence.addHandler(applySecurity(servletContextHandler, "/users", "/api/user/*"));

            server.setHandler(sequence);
        }

        @SuppressWarnings({"squid:S1172"})
        protected Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
            return servletContextHandler;
        }

        private ResourceHandler createResourceHandler() {
            ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.setDirAllowed(false);
            resourceHandler.setWelcomeFiles(START_PAGE_NAME);
            resourceHandler.setBaseResourceAsString(
                    FileSystemHelper.localFileNameOrResourceNameToFullPath(COMMON_RESOURCES_DIR));
            return resourceHandler;
        }

        private ServletContextHandler createServletContextHandler() {
            ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);

            // Аутентификация
            servletContextHandler.addServlet(new ServletHolder(new LoginServlet(templateProcessor)), "/login");
            servletContextHandler.addServlet(new ServletHolder(new LogoutServlet()), "/logout");

            // Админская страница (создание + список)
            servletContextHandler.addServlet(new ServletHolder(new AdminPageServlet(templateProcessor)), "/admin");

            // Старые сервлеты (карточка клиента, API)
            servletContextHandler.addServlet(new ServletHolder(new UserServletId(templateProcessor, dbServiceClientCache)), "/user/*");
            servletContextHandler.addServlet(new ServletHolder(new SaveUserNewClientApiServlet(dbServiceClientCache, gson)), "/api/save");
            servletContextHandler.addServlet(new ServletHolder(new UsersApiServlet(dbServiceClientCache, gson)), "/api/users");
            servletContextHandler.addServlet(new ServletHolder(new UserApiServletId(dbServiceClientCache, gson)), "/api/user/*");

            // Фильтр аутентификации — защищает всё, кроме /login
            servletContextHandler.addFilter(new FilterHolder(new AuthFilter()), "/*", EnumSet.of(DispatcherType.REQUEST));

            return servletContextHandler;



            /*
            ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            servletContextHandler.addServlet(new ServletHolder(new UsersServlet(templateProcessor, dbServiceClientCache)), "/users");
            servletContextHandler.addServlet(new ServletHolder(new UserServletId(templateProcessor, dbServiceClientCache)), "/user/*");
            servletContextHandler.addServlet(new ServletHolder(new CreateClientPageServlet()),"/save");
            servletContextHandler.addServlet(new ServletHolder(new SaveUserNewClientApiServlet(dbServiceClientCache, gson)), "/api/save");
            servletContextHandler.addServlet(new ServletHolder(new UsersApiServlet(dbServiceClientCache, gson)), "/api/users");
            servletContextHandler.addServlet(new ServletHolder(new UserApiServletId(dbServiceClientCache, gson)), "/api/user/*");
            return servletContextHandler;

             */
        }
}
