package com.mlesniak.news;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Entry point for application.
 *
 * @author Michael Lesniak (mail@mlesniak.com)
 */
@Controller
@SpringBootApplication
public class Main {
    @ResponseBody
    @RequestMapping("/")
    String root() {
        return "Hello World!";
    }

    @ResponseBody
    @RequestMapping("/admin")
    String admin() {
        return "Hello Admin!";
    }


    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }
}
