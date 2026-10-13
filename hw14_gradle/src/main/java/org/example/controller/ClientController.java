package org.example.controller;

import org.example.dto.ClientTableDto;
import org.example.service.service_client.DbServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping
    public ModelAndView listClients() {
        List<ClientTableDto> clients = dbServiceClient.findAllForTable();
        ModelAndView mav = new ModelAndView("clients-list");
        mav.addObject("clients", clients);
        return mav;
    }
    @GetMapping("/{id}/edit")
    public String editClientForm(@PathVariable Long id, Model model) {
        ClientTableDto dto = dbServiceClient.findById(id);

        // Превращаем список телефонов в строку для удобного редактирования
        if (dto.getPhoneNumbers() != null && !dto.getPhoneNumbers().isEmpty()) {
            dto.setPhoneNumbersStr(String.join(", ", dto.getPhoneNumbers()));
        } else {
            dto.setPhoneNumbersStr("");
        }

        model.addAttribute("clientDto", dto);
        return "client-edit";
    }

    @PostMapping
    public String saveClient(@ModelAttribute("clientDto") ClientTableDto dto,
                             RedirectAttributes redirectAttributes) {

        // Превращаем строку телефонов обратно в список
        if (dto.getPhoneNumbersStr() != null && !dto.getPhoneNumbersStr().isBlank()) {
            String[] parts = dto.getPhoneNumbersStr().split("\\s*,\\s*"); // Разделяем по запятой + пробелы
            List<String> numbers = new ArrayList<>();
            for (String part : parts) {
                if (!part.isBlank()) {
                    numbers.add(part);
                }
            }
            dto.setPhoneNumbers(numbers);
        } else {
            dto.setPhoneNumbers(new ArrayList<>());
        }

        // Вызываем твой сервис. Он сам поймёт: если dto.id == null → создать, иначе → обновить
        dbServiceClient.saveClient(dto);

        redirectAttributes.addFlashAttribute("message", "Клиент успешно сохранён!");
        return "redirect:/clients";
    }
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        ClientTableDto dto = new ClientTableDto();
        dto.setPhoneNumbersStr(""); // Важно: пустая строка, а не null, чтобы поле не было пустым в форме
        model.addAttribute("clientDto", dto); // ✅ Обязательно "clientDto"
        return "client-form";
    }
/*
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("client", new Client());
        return "client-form";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        Client client = dbServiceClient.findById(id); // уже с телефонами и адресом

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

        // Добавляем телефон, если он передан
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            var phone = new Phone();
            phone.setNumber(phoneNumber.trim());
            client.getPhones().add(phone); // phones уже инициализирован в Client
        }

        dbServiceClient.saveClient(client);
        log.info("Создан клиент: id={}, name={}", client.getId(), client.getName());
        return "redirect:/clients";
    }

 */
}