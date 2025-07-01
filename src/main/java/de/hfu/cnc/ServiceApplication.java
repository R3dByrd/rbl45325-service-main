// src/main/java/de/hfu/cnc/ServiceApplication.java
package de.hfu.cnc;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@SpringBootApplication
@RestController
public class ServiceApplication {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceApplication.class);
    private final Counter requestCounter;

    @Autowired
    private UserRepository repository;

    public ServiceApplication(MeterRegistry meterRegistry) {
        this.requestCounter = meterRegistry.counter("service_requests");
        LOG.info("ServiceApplication initialized; counter='service_requests' registered");
    }

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    @GetMapping("/host.html")
    public String getHostName() {
        LOG.info("Endpoint '/host.html' aufgerufen");
        try {
            requestCounter.increment();
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            LOG.error("Fehler beim Ermitteln des Hostnamens", e);
            return "Unknown Host";
        }
    }

    @RequestMapping("/")
    public String home() {
        return "Cloud Native Computing!";
    }

    @GetMapping("/users")
    public List<User> user() {
        return repository.findAll();
    }

    @PostMapping("/users")
    public void addUser(@RequestBody User user) {
        repository.insert(user);
    }

    @DeleteMapping("/users")
    public void deleteUser(@RequestParam("id") String id) {
        repository.deleteById(id);
    }
}
