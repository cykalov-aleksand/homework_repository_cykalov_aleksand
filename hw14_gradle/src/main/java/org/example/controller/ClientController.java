package org.example.controller;

import org.example.dto.ClientTableDto;
import org.example.service.DbServiceClientCache;
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
    private final DbServiceClientCache dbServiceClient;

    public ClientController(DbServiceClientCache dbServiceClient) {
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

        String phoneNumbersStr = "";
        if (dto.getPhoneNumbers() != null && !dto.getPhoneNumbers().isEmpty()) {
            phoneNumbersStr = String.join(", ", dto.getPhoneNumbers());
        }

        model.addAttribute("clientDto", dto);
        model.addAttribute("phoneNumbersStr", phoneNumbersStr);
        return "client-form";
    }

    @PostMapping
    public String saveClient(
            @ModelAttribute("clientDto") ClientTableDto dto,
            @RequestParam(name = "phoneNumbersStr", required = false) String phoneNumbersStr,
            RedirectAttributes redirectAttributes) {

        if (dto.getName() == null || dto.getName().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Имя клиента обязательно");
            return "redirect:/clients/new";
        }

        // Конвертация строки → список
        List<String> numbers = new ArrayList<>();
        if (phoneNumbersStr != null && !phoneNumbersStr.isBlank()) {
            String[] parts = phoneNumbersStr.split("\\s*,\\s*");
            for (String part : parts) {
                if (!part.isBlank()) {
                    numbers.add(part);
                }
            }
        }
        dto.setPhoneNumbers(numbers);

        dbServiceClient.saveClient(dto);

        redirectAttributes.addFlashAttribute("message", "Клиент успешно сохранён!");
        return "redirect:/clients";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        ClientTableDto dto = new ClientTableDto();
        model.addAttribute("clientDto", dto);
        model.addAttribute("phoneNumbersStr", "");
        return "client-form";
    }

    @PostMapping("/{id}/delete")
    public String deleteClient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            dbServiceClient.deleteClient(id);
            redirectAttributes.addFlashAttribute("message", "Клиент с ID " + id + " успешно удалён!");
            log.info("Клиент ID {} удалён пользователем", id);
        } catch (IllegalArgumentException e) {
            log.warn("Попытка удалить несуществующего клиента ID {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Не удалось удалить: клиент не найден.");
        } catch (Exception e) {
            log.error("Ошибка при удалении клиента ID {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Произошла ошибка при удалении.");
        }
        return "redirect:/clients";
    }

    @GetMapping("/delete/{id}")
    public String redirectToDelete(@PathVariable Long id) {
        return "redirect:/clients";
    }
}