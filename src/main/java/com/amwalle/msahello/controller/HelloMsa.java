package com.amwalle.msahello.controller;

import com.amwalle.msahello.util.Address;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloMsa {
    @RequestMapping(name = "HelloService", method = RequestMethod.GET, path = "/hello-msa")
    public String helloMsa() {
        String hostAddress = Address.getIpAddress();
        return "Hello, this service is from " + hostAddress + ".";
    }
}
