package org.example.servlet.json;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.crm.model.Client;
import org.example.dao.DbServiceClientCache;

import java.io.IOException;

public class UserApiServletId extends HttpServlet {
    private static final int ID_PATH_PARAM_POSITION = 1;

    private final transient DbServiceClientCache dbServiceClientCache;
    private final transient Gson gson;

    public UserApiServletId(DbServiceClientCache dbServiceClientCache, Gson gson) {
        this.dbServiceClientCache = dbServiceClientCache;
        this.gson = gson;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Client user = dbServiceClientCache.getClient(extractIdFromRequest(request)).orElse(null);

        // Разрываем циклическую ссылку для Gson
        if (user != null && user.getPhones() != null) {
            user.getPhones().forEach(phone -> phone.setClient(null));
        }

        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.print(gson.toJson(user));
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
