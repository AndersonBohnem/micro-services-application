package org.insertcoin.greetingservice.controllers;

import org.insertcoin.greetingservice.configs.GreetingConfig;
import org.insertcoin.greetingservice.dto.GreetingDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("greeting")
public class GreetingController {

    public GreetingController(GreetingConfig config) {
        this.config = config;
    }

    private final GreetingConfig config;

    @GetMapping({"", "/{pathName}"})
    public ResponseEntity<String> greeting(
            @PathVariable Optional<String> pathName,
            @RequestParam(required = false) String queryName
    ) {
        String greetingReturn = config.getGreeting();

        String nameReturn = queryName != null
                ? queryName
                : pathName.orElse(config.getDefaultName());

        String textReturn = String.format("%s, %s!!!", greetingReturn, nameReturn);

        return ResponseEntity.ok(textReturn);
    }

    @PostMapping
    public ResponseEntity<String> greetingPost(
            @RequestBody GreetingDto request
    ) {
        String greetingReturn = config.getGreeting();

        String nameReturn = request.getName();

        String textReturn = String.format("%s, %s!!!", greetingReturn, nameReturn);

        return ResponseEntity.ok(textReturn);
    }

}
