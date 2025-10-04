package com.project.api.controller;


import com.project.common.Request;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RestApiTest {

    @GetMapping("/get/{name}")
    public String get(@PathVariable("name") String name) {
        return "Hello " + name;
    }

    @PostMapping("/post")
    public String post(@RequestBody Request request) {
        return "post " + request.toString();
    }

    @PutMapping("/put/{id}")
    public String put(@PathVariable("id") Long id,
                      @RequestBody Request request) {
        return "put id=" + id + ", body=" + request.toString();
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        return "delete id=" + id;
    }
}
