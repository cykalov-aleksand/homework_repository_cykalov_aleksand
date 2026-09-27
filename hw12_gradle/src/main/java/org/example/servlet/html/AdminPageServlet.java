package org.example.servlet.html;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.services.TemplateProcessor;

import java.io.IOException;
import java.util.HashMap;

public class AdminPageServlet extends HttpServlet {
    private final transient TemplateProcessor templateProcessor;

    public AdminPageServlet(TemplateProcessor templateProcessor) {
        this.templateProcessor = templateProcessor;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().println(templateProcessor.getPage("admin.html", new HashMap<>()));
    }
}
