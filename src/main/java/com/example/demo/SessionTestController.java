package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
public class SessionTestController {

    @GetMapping("/")
    @ResponseBody
    public String sessionTest(HttpSession session){
        log.info("freeMemory = {}",Runtime.getRuntime().freeMemory());

        session.setMaxInactiveInterval(3);

        for(int i =0 ; i < 100000 ; i++){
            session.setAttribute(String.valueOf(i), i);
        }
        session.invalidate();
        log.info("session.getAttribute(\"0\") = {}",session.getAttribute("0"));

        log.info("freeMemory = {}",Runtime.getRuntime().freeMemory());
      return "session";
    }

    @GetMapping("/get")
    @ResponseBody
    public String get(HttpSession session){
        log.info("session.getAttribute(\"0\") = {}",session.getAttribute("0"));
        return "session";
    }
}
