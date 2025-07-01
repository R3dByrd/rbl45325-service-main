package de.hfu.cnc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
public class RestClient {

    private final WebClient webClient;

    public RestClient(WebClient.Builder webClientBuilder,
                      @Value("${service.backend}") String serviceBackend) {
        this.webClient = webClientBuilder
            .baseUrl(serviceBackend)
            .build();
    }

    @RequestMapping("/firstUser")
    public User firstUser() {
        List<User> users = this.webClient
            .get()
            .uri("/users")
            .retrieve()
            .bodyToFlux(User.class)
            .collectList()
            .block();
        return (users != null && !users.isEmpty()) ? users.get(0) : null;
    }
}
