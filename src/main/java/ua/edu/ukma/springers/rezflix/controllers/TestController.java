package ua.edu.ukma.springers.rezflix.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.TestControllerApi;

@RestController
public class TestController implements TestControllerApi {

    @Override
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Hello, World!");
    }
}
