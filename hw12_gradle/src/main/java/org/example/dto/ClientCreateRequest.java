package org.example.dto;

public class ClientCreateRequest {
    private String name;
    private String street;
    private java.util.List<String> phoneNumbers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public java.util.List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(java.util.List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}
