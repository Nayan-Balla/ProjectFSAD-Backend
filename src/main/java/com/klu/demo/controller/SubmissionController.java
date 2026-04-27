package com.klu.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.klu.demo.entity.Submission;
import com.klu.demo.service.SubmissionService;
import java.util.List;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {
    @Autowired private SubmissionService svc;

    @PostMapping public ResponseEntity<?> add(@RequestBody Submission s){ return ResponseEntity.ok(svc.add(s)); }
    @GetMapping  public List<Submission> all(){ return svc.all(); }
    @PutMapping("/{id}") public ResponseEntity<?> upd(@PathVariable Long id,@RequestBody Submission s){
        try{return ResponseEntity.ok(svc.update(id,s));}catch(IllegalArgumentException e){return ResponseEntity.notFound().build();}
    }
    @DeleteMapping("/{id}") public ResponseEntity<?> del(@PathVariable Long id){ svc.delete(id); return ResponseEntity.noContent().build(); }
}

