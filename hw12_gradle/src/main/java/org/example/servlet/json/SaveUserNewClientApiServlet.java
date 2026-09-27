package org.example.servlet.json;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dto.ClientResponse;
import org.example.dto.ErrorResponse;
import org.example.crm.model.Address;
import org.example.crm.model.Client;
import org.example.crm.model.Phone;
import org.example.dao.DbServiceClientCache;
import org.example.dto.ClientCreateRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SaveUserNewClientApiServlet extends HttpServlet {
    private final DbServiceClientCache dbServiceClientCache;
    private final transient Gson gson;

    public SaveUserNewClientApiServlet(DbServiceClientCache dbServiceClientCache, Gson gson) {
        this.dbServiceClientCache = dbServiceClientCache;
        this.gson = gson;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ClientCreateRequest request = gson.fromJson(req.getReader(), ClientCreateRequest.class);

            if (request.getName() == null || request.getName().isBlank()) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Поле 'name' обязательно");
                return;
            }

            Address address = new Address(null, request.getStreet()); // ID = null

            List<Phone> phones = new ArrayList<>();
            if (request.getPhoneNumbers() != null) {
                for (String number : request.getPhoneNumbers()) {
                    phones.add(new Phone(null, number));
                }
            }

            Client client = new Client(request.getName());
            client.setAddress(address);

            for (Phone p : phones) {
                client.addPhone(p);
            }
            Long savedId = dbServiceClientCache.saveClient(client).getId();

            // Возвращаем JSON с результатом
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json;charset=UTF-8");

            String responseJson = gson.toJson(new ClientResponse(savedId, request.getName()));
            resp.getWriter().write(responseJson);

        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при создании: " + e.getMessage());
        }
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        String errorJson = gson.toJson(new ErrorResponse(message));
        resp.getWriter().write(errorJson);
    }
}
