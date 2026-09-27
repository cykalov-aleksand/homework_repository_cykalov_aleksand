package org.example.servlet.html;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.services.TemplateProcessor;

import java.io.IOException;
import java.util.HashMap;

public class LoginServlet extends HttpServlet {

    private static final String LOGIN_PAGE = "login.html";
    private static final String ADMIN_LOGIN = "User7";
    private static final String ADMIN_PASSWORD = "11111";

    private final transient TemplateProcessor templateProcessor;

    public LoginServlet(TemplateProcessor templateProcessor) {
        this.templateProcessor = templateProcessor;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().println(templateProcessor.getPage(LOGIN_PAGE, new HashMap<>()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");

        if (ADMIN_LOGIN.equals(login) && ADMIN_PASSWORD.equals(password)) {
            // Сохраняем в сессию признак аутентификации
            req.getSession().setAttribute("authenticated", true);
            // Редирект на админскую страницу
            resp.sendRedirect("/admin");
        } else {
            // Возвращаем страницу логина с ошибкой
            resp.setContentType("text/html;charset=UTF-8");
            var params = new HashMap<String, Object>();
            params.put("error", true);
            resp.getWriter().println(templateProcessor.getPage(LOGIN_PAGE, params));
        }
    }
}
