package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.ClientTableDto;
import org.example.exception.DuplicateClientNameException;
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
@SessionAttributes("clientDto")
@RequiredArgsConstructor
public class ClientController {

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);
    private final DbServiceClientCache dbServiceClient;

    // --- Список клиентов ---
    @GetMapping("/clients")
    public ModelAndView listClients() {
        List<ClientTableDto> clients = dbServiceClient.findAllForTable();
        ModelAndView mav = new ModelAndView("clients-list");
        mav.addObject("clients", clients);
        return mav;
    }

    // --- Форма создания нового клиента ---
    @GetMapping("/clients/new")
    public String showCreateForm(Model model) {
        ClientTableDto dto = new ClientTableDto();
        model.addAttribute("clientDto", dto);
        model.addAttribute("phoneNumbersStr", "");
        return "client-form";
    }

    // --- Сохранение (Create & Update) ---
    @PostMapping("/clients")
    public String saveClient(
            @ModelAttribute("clientDto") ClientTableDto dto,
            @RequestParam(name = "phoneNumbersStr", required = false) String phoneNumbersStr,
            RedirectAttributes redirectAttributes) {

        if (dto.getName() == null || dto.getName().isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Имя клиента обязательно");
            return "redirect:/clients/new";
        }

        // Парсинг телефонов
        List<String> numbers = new ArrayList<>();
        if (phoneNumbersStr != null && !phoneNumbersStr.isBlank()) {
            for (String raw : phoneNumbersStr.split(",")) {
                String clean = raw.strip().replaceAll("[^\\d+]", "");
                if (!clean.isBlank() && clean.length() >= 7) {
                    numbers.add(clean);
                }
            }
        }
        dto.setPhoneNumbers(numbers.isEmpty() ? List.of() : numbers);

        try {
            dbServiceClient.saveClient(dto);
            String msg = (dto.getId() != null) ? "Клиент успешно обновлён!" : "Клиент успешно создан!";
            redirectAttributes.addFlashAttribute("message", msg);
            return "redirect:/clients";
        } catch (DuplicateClientNameException e) {
            log.warn("Дубликат имени: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            // Если это был ре-директ после редактирования, вернём на форму редактирования
            String redirectPath = (dto.getId() != null)
                    ? "/clients/" + dto.getId() + "/edit"
                    : "/clients/new";
            return "redirect:" + redirectPath;
        } catch (Exception e) {
            log.error("Ошибка сохранения", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Произошла непредвиденная ошибка");
            String redirectPath = (dto.getId() != null)
                    ? "/clients/" + dto.getId() + "/edit"
                    : "/clients/new";
            return "redirect:" + redirectPath;
        }
    }

    // --- РЕДАКТИРОВАНИЕ: форма и логика ---
    @GetMapping("/{id}/edit")
    public String editClientForm(@PathVariable Long id, Model model) {
        // Сначала ищем. Если не найден — будет исключение, которое перехватит @ControllerAdvice (или стандартная 500)
        ClientTableDto dto = dbServiceClient.findById(id);

        String phoneNumbersStr = "";
        if (dto.getPhoneNumbers() != null && !dto.getPhoneNumbers().isEmpty()) {
            phoneNumbersStr = String.join(", ", dto.getPhoneNumbers());
        }

        model.addAttribute("clientDto", dto);
        model.addAttribute("phoneNumbersStr", phoneNumbersStr);
        return "client-form"; // Тот же шаблон, что и для создания
    }

    // --- УДАЛЕНИЕ ---
    @PostMapping("/clients/{id}/delete")
    public String deleteClient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            dbServiceClient.deleteClient(id);
            redirectAttributes.addFlashAttribute("message", "Клиент с ID " + id + " успешно удалён!");
            log.info("Клиент ID {} удалён", id);
        } catch (IllegalArgumentException e) {
            log.warn("Не найден клиент ID {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Клиент не найден.");
        } catch (Exception e) {
            log.error("Ошибка при удалении клиента ID {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении.");
        }
        return "redirect:/clients";
    }
}