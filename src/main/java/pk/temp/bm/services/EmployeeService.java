package pk.temp.bm.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pk.temp.bm.models.EmployeeModel;
import pk.temp.bm.repositories.EmployeeRepository;

import java.io.IOException;
import java.util.List;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    public EmployeeModel findById(Long empId){
        return employeeRepository.findOne(empId);
    }

    public Boolean createEmployee(String employeeJSON){
        Boolean actionStatus = false;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            EmployeeModel employeeModel = objectMapper.readValue(employeeJSON,EmployeeModel.class);
            employeeRepository.save(employeeModel);
            actionStatus = false;
        } catch (IOException e) {
            logger.error("Error While creating new Employee."+e.getMessage(),e);
        }
        return actionStatus;
    }

    public List<EmployeeModel> getAllEmployees(){
        return (List<EmployeeModel>) employeeRepository.findAll();
    }
}
