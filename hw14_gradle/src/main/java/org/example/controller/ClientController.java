package org.example.controller;

import org.example.crm.model.Client;
import org.example.crm.model.Phone;
import org.example.service.DbServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/clients")
public class ClientController {
    private static final Logger log = LoggerFactory.getLogger(ClientController.class);

    private final DbServiceClient dbServiceClient;

    public ClientController(DbServiceClient dbServiceClient) {
        this.dbServiceClient = dbServiceClient;
    }

    // Список всех клиентов (страница)
    @GetMapping
    public String list(Model model) {
        List<Client> clients = dbServiceClient.findAll(); // только лёгкие объекты
        model.addAttribute("clients", clients);
        return "client-list";
    }

    // Страница создания клиента (форма)
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("client", new Client());
        return "client-form";
    }

   // Сохранение клиента
   // @PostMapping
   // public String create(@ModelAttribute Client client) {
   //     dbServiceClient.saveClient(client);
   //     return "redirect:/clients";
   // }

    // Просмотр одного клиента
    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        Client client = dbServiceClient.getClient(id);

        if (client == null) {
            model.addAttribute("error", "Клиент не найден");
            return "error";
        }

        model.addAttribute("client", client);
        return "client-view";
    }

    @PostMapping
    public String create(@ModelAttribute Client client,
                         @RequestParam(required = false) String phoneNumber,
                         Model model) {
        // Если телефон передан — добавляем его к клиенту перед сохранением
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            Phone phone = new Phone();
            phone.setNumber(phoneNumber.trim());
            // ID клиента будет установлен внутри saveClient
            if (client.getPhones() == null) {
                client.setPhones(new ArrayList<>());
            }
            client.getPhones().add(phone);
        }

        dbServiceClient.saveClient(client);
        return "redirect:/clients";
    }


      @GetMapping("/clients/{id}")
    public Client getClientWithPhones(@PathVariable Long id) {
        return dbServiceClient.findById(id); // вернёт Client с заполненным phones
    }
}
