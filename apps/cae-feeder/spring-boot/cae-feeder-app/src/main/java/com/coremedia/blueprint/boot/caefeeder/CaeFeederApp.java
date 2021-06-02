package com.coremedia.blueprint.boot.caefeeder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CaeFeederApp {

  // ... Bean definitions
  public static void main(String[] args) {
    SpringApplication.run(CaeFeederApp.class, args);
  }
}
