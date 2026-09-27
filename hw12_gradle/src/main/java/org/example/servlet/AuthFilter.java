package org.example.servlet;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Разрешаем доступ к странице логина и самому процессу логина
        String path = req.getRequestURI();
        if (path.equals("/login") || path.equals("/") || path.equals("/index.html")) {
            chain.doFilter(request, response);
            return;
        }

        // Проверяем аутентификацию
        HttpSession session = req.getSession(false);
        if (session != null && Boolean.TRUE.equals(session.getAttribute("authenticated"))) {
            chain.doFilter(request, response);
        } else {
            resp.sendRedirect("/login");
        }
    }
}
