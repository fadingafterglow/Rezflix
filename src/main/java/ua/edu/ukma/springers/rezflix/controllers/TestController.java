package ua.edu.ukma.springers.rezflix.controllers;

import org.springframework.http.ResponseEntity;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.TestControllerApi;

public class TestController implements TestControllerApi {

    @Override
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Hello, World!");
    }
}
