package pk.temp.bm.controller;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pk.temp.bm.models.EmployeeSalaryModel;
import pk.temp.bm.services.EmployeeSalaryService;

import java.util.Date;
import java.util.List;

@RestController
public class EmployeeSalaryController {

    @Autowired
    private EmployeeSalaryService employeeSalaryService;

    private static final Logger logger = LoggerFactory.getLogger(EmployeeSalaryController.class);


    @RequestMapping(value = "/paySalary")
    public ResponseEntity<?> paySalary(@RequestParam("employeeId") Long employeeId, @RequestParam("amount") Double amountPaid,
                                       @RequestParam("date") Date date){
        Boolean recordUpdated = false;
        try{
            employeeSalaryService.paySalary(employeeId,amountPaid,date);
            recordUpdated = true;
        }
        catch (Exception e){
            logger.error("Exception while updating salary record. : "+e.getMessage(),e);
        }

        return new ResponseEntity<Object>(recordUpdated ? HttpStatus.ACCEPTED : HttpStatus.NOT_ACCEPTABLE);
    }

    @RequestMapping(value = "/getEmployeeSalaryHistory")
    public List<EmployeeSalaryModel> getEmployeeSalaryHistory(@RequestParam("employeeId") Long employeeId){
        return employeeSalaryService.getEmployeeSalaryHistory(employeeId);
    }

    @RequestMapping(value = "/getCurrentPayableSalary")
    public JSONObject getCurrentPayableSalary(/*@RequestParam("empId") Long empId*/){
        Long empId = 1L;
        return employeeSalaryService.getCurrentSalary(empId);
    }

}
