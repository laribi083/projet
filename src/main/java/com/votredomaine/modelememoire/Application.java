package com.votredomaine.modelememoire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("Application démarrée sur http://localhost:8082");
        System.out.println("Page d'accueil: http://localhost:8082/");
        System.out.println("Page create: http://localhost:8082/create");
        System.out.println("Page forgot: http://localhost:8082/forgot");
    }
}
