package haru.pharmacy.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/secret")
@Hidden
public class EasterEggController {

    @GetMapping("/cornet")
    public ResponseEntity<Map<String, String>> getCornetStrategy() {
        return ResponseEntity
                .status(HttpStatus.I_AM_A_TEAPOT)
                .header("X-Character", "Konata Izumi")
                .body(Map.of(
                        "question", "Which end is the head?",
                        "answer", "The thick end is the head!",
                        "quote", "Eating the chocolate cornet is an art."
                ));
    }
}
