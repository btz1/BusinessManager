package pk.temp.bm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pk.temp.bm.services.EmployeeService;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @RequestMapping(value = "/createEmployee")
    public void createEmployee(@RequestParam("employeeJSON") String employeeJSON){
        employeeService.createEmployee(employeeJSON);
    }

}
