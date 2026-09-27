package org.example.servlet.html;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;

public class CreateClientPageServlet extends HttpServlet {
    private static final String TEMPLATE_PATH = "templates/saveuser.html";

        @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(TEMPLATE_PATH)) {
            if (inputStream == null) {
                System.err.println("ОШИБКА: Файл не найден по пути: " + TEMPLATE_PATH);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Шаблон не найден: " + TEMPLATE_PATH);
                return;
            }
            byte[] bytes = inputStream.readAllBytes();
            resp.setContentType("text/html;charset=UTF-8");
            resp.setContentLength(bytes.length);
            resp.getOutputStream().write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка чтения шаблона: " + e.getMessage());
        }
    }
}
