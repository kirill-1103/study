package ru.krey.microservices.customer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@Slf4j
public record CustomerController(CustomerService customerService) {
    @PostMapping
    public void registerCustomer(@RequestBody CustomerRegestrationRequest customerRequest){
        log.info("new cutomer registration {}",customerRequest);
        customerService.registrationCustomer(customerRequest);
    }
    @GetMapping("{name}")
    public String someRequest(@PathVariable String name){
        return name;
    }
}
