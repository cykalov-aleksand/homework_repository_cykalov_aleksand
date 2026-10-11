package org.example.service;

import org.example.crm.model.Address;
import org.example.crm.model.Client;
//import org.example.crm.model.Phone;
import org.example.crm.model.Phone;
import org.example.crm.repository.AddressRepository;
import org.example.crm.repository.ClientRepository;
//import org.example.crm.repository.PhoneRepository;
import org.example.crm.repository.PhoneRepository;
import org.example.sessionmanager.TransactionManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DbServiceClientImpl implements DbServiceClient {

    private final TransactionManager transactionManager;
    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final PhoneRepository phoneRepository;

    public DbServiceClientImpl(TransactionManager transactionManager,
                               ClientRepository clientRepository,
                               AddressRepository addressRepository,
                               PhoneRepository phoneRepository) {
        this.transactionManager = transactionManager;
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
        this.phoneRepository = phoneRepository;
    }
    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(() -> {
            Address address;

            // --- РАБОТА С АДРЕСОМ ---
            if (client.getAddressId() == null) {
                // Случай 1: Адреса вообще нет в БД (новый клиент или потерянная связь)
                String streetName;

                // ВАЖНО: Сначала проверяем сам объект address, а не только ID
                if (client.getAddress() != null && client.getAddress().getStreet() != null) {
                    streetName = client.getAddress().getStreet();
                } else {
                    // Фолбэк: генерируем имя улицы, если данных нет
                    streetName = "Улица для " + (client.getName() != null ? client.getName() : "клиента");
                }

                address = new Address();
                address.setStreet(streetName);
                address = addressRepository.save(address);

                // Привязываем новый адрес к клиенту
                client.setAddressId(address.getId());

                // Опционально: если в объекте клиента поле address было null,
                // можно положить туда свежесохранённый адрес, чтобы дальше по коду не было null
                if (client.getAddress() == null) {
                    client.setAddress(address);
                }
            } else {
                // Случай 2: Адрес уже есть (есть ID), пытаемся его обновить
                Optional<Address> existingOpt = addressRepository.findById(client.getAddressId());

                if (existingOpt.isPresent()) {
                    address = existingOpt.get();

                    // Обновляем улицу, только если она явно передана в объекте Client
                    if (client.getAddress() != null && client.getAddress().getStreet() != null) {
                        address.setStreet(client.getAddress().getStreet());
                        addressRepository.save(address); // Сохраняем изменения адреса
                    }
                    // Если улица не передана — ничего не делаем, оставляем старую
                } else {
                    // Случай 3: ID есть, но записи в БД нет (битые данные)
                    // Создаём новый адрес и обновляем ID у клиента
                    address = new Address();
                    address.setStreet("Улица для " + client.getName());
                    address = addressRepository.save(address);
                    client.setAddressId(address.getId());
                }
            }

            // --- СОХРАНЕНИЕ КЛИЕНТА ---
            Client savedClient = clientRepository.save(client);

            // --- РАБОТА С ТЕЛЕФОНАМИ ---
            // Проверка на null и на пустой список
            if (client.getPhones() != null && !client.getPhones().isEmpty()) {
                for (Phone phone : client.getPhones()) {
                    phone.setClientId(savedClient.getId());

                    // Если у телефона нет ID, он создастся. Если есть — обновится.
                    // Это стандартное поведение save() в Spring Data JDBC/Hibernate
                    phoneRepository.save(phone);
                }
            }

            return savedClient;
        });
    }
    // Метод для добавления телефона отдельно (удобно для тестов и эндпоинтов)
    public Phone addPhoneToClient(Long clientId, String number) {
        Phone phone = new Phone();
        phone.setNumber(number);
        phone.setClientId(clientId);
        return phoneRepository.save(phone);
    }

    /**
     * Возвращает Client с подгруженными телефонами.
     * Если клиента нет — возвращает null.
     */
    @Override
    public Client getClientWithDetails(Long id) {
        return transactionManager.doInTransaction(() -> {
            Client client = clientRepository.findById(id).orElse(null);
            if (client == null) {
                return null;
            }

            // ИСПРАВЛЕНИЕ: безопасно превращаем Iterable в List
            List<Phone> phones = new ArrayList<>();
            phoneRepository.findByClientId(client.getId()).forEach(phones::add);
            client.setPhones(phones);

            return client;
        });
    }
    /**
     * Основной метод для кэша и контроллеров.
     * Возвращает Client или null.
     */
    @Override
    public Client findById(Long id) {
        return transactionManager.doInTransaction(() -> {
            Client client = clientRepository.findById(id).orElse(null);
            if (client == null) {
                return null;
            }

            // ИСПРАВЛЕНИЕ: тоже конвертируем Iterable в List
            List<Phone> phones = new ArrayList<>();
            phoneRepository.findByClientId(client.getId()).forEach(phones::add);
            client.setPhones(phones);

            // Подгружаем адрес
            if (client.getAddressId() != null) {
                Optional<Address> addressOpt = addressRepository.findById(client.getAddressId());
                addressOpt.ifPresent(client::setAddress);
            }

            return client;
        });
    }
        /**
     * Лёгкий метод, если нужно просто проверить наличие клиента без телефонов.
     * Можно использовать внутри других сервисов.
     */
    @Override
    public Client getClient(Long id) {
        return findById(id);
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInTransaction(() -> {
            List<Client> list = new ArrayList<>();
            clientRepository.findAll().forEach(list::add);
            return list;
        });
    }
}