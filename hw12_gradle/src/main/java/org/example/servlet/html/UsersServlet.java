package org.example.servlet.html;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.crm.model.Client;
import org.example.dao.DbServiceClientCache;
import org.example.services.TemplateProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersServlet extends HttpServlet {

        private static final String USERS_PAGE_TEMPLATE = "users.html";

        private final transient DbServiceClientCache dbServiceClientCache;
        private final transient TemplateProcessor templateProcessor;

        public UsersServlet(TemplateProcessor templateProcessor, DbServiceClientCache dbServiceClientCache) {
            this.templateProcessor = templateProcessor;
            this.dbServiceClientCache = dbServiceClientCache;
        }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        Map<String, Object> paramsMap = new HashMap<>();

        // Получаем ВСЕ клиенты из БД (или из кэша, если он настроен верно)
        List<Client> clients = dbServiceClientCache.findAll();

        // Кладем весь список в параметры для шаблона
        paramsMap.put("clients", clients);

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(templateProcessor.getPage(USERS_PAGE_TEMPLATE, paramsMap));
    }
    }
