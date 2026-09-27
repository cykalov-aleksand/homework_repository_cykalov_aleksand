package org.example.servlet.html;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.crm.model.Client;
import org.example.dao.DbServiceClientCache;
import org.example.services.TemplateProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserServletId extends HttpServlet {
    private static final String USERS_PAGE_TEMPLATE = "user.html";
    private static final int ID_PATH_PARAM_POSITION = 1;

    private final transient DbServiceClientCache dbServiceClientCache;
    private final transient TemplateProcessor templateProcessor;

    public UserServletId(TemplateProcessor templateProcessor, DbServiceClientCache dbServiceClientCache) {
        this.templateProcessor = templateProcessor;
        this.dbServiceClientCache = dbServiceClientCache;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        Map<String, Object> paramsMap = new HashMap<>();
        long id = extractIdFromRequest(req);
        Client client = dbServiceClientCache.getClient(id).orElse(null);
        paramsMap.put("clients", client);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(templateProcessor.getPage(USERS_PAGE_TEMPLATE, paramsMap));
    }
    private long extractIdFromRequest(HttpServletRequest request) {
        String[] path = request.getPathInfo().split("/");
        try {
            return (path.length > 1) ? Long.parseLong(path[ID_PATH_PARAM_POSITION]) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

