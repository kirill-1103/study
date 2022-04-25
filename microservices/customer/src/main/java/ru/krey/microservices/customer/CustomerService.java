package ru.krey.microservices.customer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public record CustomerService(CustomerRepo customerRepo,  RestTemplate restTemplate) {
    public void registrationCustomer(CustomerRegestrationRequest customerRequest) {
        Customer customer = Customer.builder()
                .firstName(customerRequest.firstName())
                .lastName(customerRequest.lastName())
                .email(customerRequest.email())
                .build();

        customerRepo.saveAndFlush(customer);

        /*HTTP talking*/
//        FraudCheckResponse fraudCheckResponse = restTemplate.getForObject(
//                "http://localhost:8081/api/v1/fraud-check/{customerId}",
//                FraudCheckResponse.class,
//                customer.getId()
//        );

        /*discovery talking*/
        FraudCheckResponse fraudCheckResponse = restTemplate.getForObject(
                "http://FRAUD/api/v1/fraud-check/{customerId}",
                FraudCheckResponse.class,
                customer.getId()
        );


        if(fraudCheckResponse.isFraudster()){
            throw new IllegalStateException("fraudster");
        }

    }
}
