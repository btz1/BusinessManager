package pk.temp.bm.controller;


import org.hibernate.boot.jaxb.SourceType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.print.DocFlavor;

@RestController
public class TestController {


    @RequestMapping(value = "/test1" ,method = RequestMethod.GET)
    public String test(){
        return "Evrything Good Under the Hood!";

    }



}
