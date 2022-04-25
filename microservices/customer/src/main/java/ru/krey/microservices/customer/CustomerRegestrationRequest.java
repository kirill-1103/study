package ru.krey.microservices.customer;

public record CustomerRegestrationRequest(String firstName, String lastName, String email) {
}
