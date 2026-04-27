package com.klu.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.klu.demo.entity.Registration;
import com.klu.demo.service.RegistrationService;
import java.util.List;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {
    @Autowired private RegistrationService svc;
    @PostMapping public Registration add(@RequestBody Registration r){ return svc.add(r); }
    @GetMapping public List<Registration> all(){ return svc.all(); }
}

