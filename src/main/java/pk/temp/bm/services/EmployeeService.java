package pk.temp.bm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pk.temp.bm.models.EmployeeModel;
import pk.temp.bm.repositories.EmployeeRepository;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public EmployeeModel findById(Long empId){
        return employeeRepository.findOne(empId);
    }
}
