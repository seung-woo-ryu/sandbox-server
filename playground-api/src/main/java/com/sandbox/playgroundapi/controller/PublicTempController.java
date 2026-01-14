package com.sandbox.playgroundapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.sandbox.playgroundapi.config.constant.ApiPrefixConstant.PUBLIC_API_V1;

@RestController
@RequestMapping(PUBLIC_API_V1)
public class PublicTempController {
    @GetMapping("/test")
    public ResponseEntity<String> test() {

        return ResponseEntity.ok("success");
    }
}
