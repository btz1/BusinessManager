package pk.temp.bm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pk.temp.bm.models.EmployeeModel;
import pk.temp.bm.models.EmployeeSalaryModel;
import pk.temp.bm.repositories.AttendanceRepository;
import pk.temp.bm.repositories.EmployeeSalaryRepository;

import java.util.Date;
import java.util.List;

@Service
public class EmployeeSalaryService {

    @Autowired
    private EmployeeSalaryRepository employeeSalaryRepository;
    @Autowired
    private AttendanceService attendanceService;

    public void paySalary(Long employeeId, Double amountPaid, Date paidOn){
        EmployeeSalaryModel employeeSalaryModel = new EmployeeSalaryModel();
        EmployeeModel employeeModel = new EmployeeModel();
        employeeModel.setId(employeeId);

        employeeSalaryModel.setEmployee(employeeModel);
        employeeSalaryModel.setPaidAmount(amountPaid);
        employeeSalaryModel.setDate(paidOn);

        employeeSalaryRepository.save(employeeSalaryModel);
    }

    public List<EmployeeSalaryModel> getEmployeeSalaryHistory(Long empId){
        EmployeeModel employeeModel = new EmployeeModel();
        employeeModel.setId(empId);
        return employeeSalaryRepository.findByEmployee();
    }


}
