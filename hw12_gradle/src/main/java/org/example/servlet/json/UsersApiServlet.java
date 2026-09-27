package org.example.servlet.json;

import com.google.gson.Gson;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.crm.model.Client;
import org.example.dao.DbServiceClientCache;

import java.io.IOException;
import java.util.List;

public class UsersApiServlet extends HttpServlet {
    private final transient DbServiceClientCache dbServiceClientCache;
    private final transient Gson gson;

    public UsersApiServlet(DbServiceClientCache dbServiceClientCache, Gson gson) {
        this.dbServiceClientCache = dbServiceClientCache;
        this.gson = gson;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Client> users = dbServiceClientCache.findAll();
        if (users != null) {
            for (Client user : users) {
                if (user.getPhones() != null) {
                    user.getPhones().forEach(phone -> phone.setClient(null));
                }
            }
        }
       response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.print(gson.toJson(users));
    }
}
