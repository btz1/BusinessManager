package pk.temp.bm.services;

import jdk.nashorn.internal.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pk.temp.bm.models.EmployeeModel;
import pk.temp.bm.models.EmployeeSalaryModel;
import pk.temp.bm.repositories.AttendanceRepository;
import pk.temp.bm.repositories.EmployeeSalaryRepository;
import pk.temp.bm.utilities.BMDateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class EmployeeSalaryService {

    @Autowired
    private EmployeeSalaryRepository employeeSalaryRepository;
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private EmployeeService employeeService;

    public void paySalary(String salaryList){
        JSONObject jsonObject = new JSONObject();
        EmployeeSalaryModel employeeSalaryModel = new EmployeeSalaryModel();
        EmployeeModel employeeModel = new EmployeeModel();
        employeeModel.setId(salaryList.empId);

        employeeSalaryModel.setEmployee(employeeModel);
        employeeSalaryModel.setPaidAmount(amountPaid);
        employeeSalaryModel.setDate(paidOn);

        employeeSalaryRepository.save(employeeSalaryModel);
    }

    public List<EmployeeSalaryModel> getEmployeeSalaryHistory(Long empId){
        EmployeeModel employeeModel = new EmployeeModel();
        employeeModel.setId(empId);
        return employeeSalaryRepository.findByEmployee(employeeModel);
    }

    public JSONArray getEmployeePayableSalaryList(){
        List<EmployeeModel> allActiveEmployees = employeeService.getActiveEmployees();
        JSONArray employeeSalaryList = new JSONArray();
        for(EmployeeModel employeeModel : allActiveEmployees){
            employeeSalaryList.add(getCurrentSalary(employeeModel.getId()));
        }
        return employeeSalaryList;
    }

    public JSONObject getCurrentSalary(Long empId){
        EmployeeModel employeeModel;
        Double currentSalary;
        JSONObject jsonObject = new JSONObject();
        Date currentDate = new Date();
        Date monthStart = BMDateUtils.getStartDateOfMonth(currentDate);
        monthStart = BMDateUtils.getDateForStartOfDay(monthStart);
        Date monthEnd = BMDateUtils.getEndDateOfMonth(currentDate);
        monthEnd = BMDateUtils.getDateForEndOfDay(monthEnd);

        double alreadyPaidThisMonth = 0;
        List<EmployeeSalaryModel> salaryHistoryOfMonth = employeeSalaryRepository.findByDateBetween(monthStart,monthEnd);
        if(salaryHistoryOfMonth.isEmpty()){
            employeeModel = employeeService.findById(empId);
        } else {
            JSONArray jsonArray = new JSONArray();
            employeeModel = salaryHistoryOfMonth.get(0).getEmployee();
            for(EmployeeSalaryModel salaryModel : salaryHistoryOfMonth){
                JSONObject detail = new JSONObject();
                detail.put("paidOn",BMDateUtils.getMySQLDateString(salaryModel.getDate()));
                detail.put("paidAmount",salaryModel.getPaidAmount());
                jsonArray.add(detail);
                alreadyPaidThisMonth += salaryModel.getPaidAmount();
            }
            jsonObject.put("paymentDetail",jsonArray);
        }
        currentSalary = employeeModel.getPerMonthSalary() - alreadyPaidThisMonth;
        jsonObject.put("totalSalary",employeeModel.getPerMonthSalary());
        jsonObject.put("currentPayable",currentSalary);
        jsonObject.put("employeeId",employeeModel.getId());
        jsonObject.put("employeeName",employeeModel.getName());
        jsonObject.put("salaryMonth",BMDateUtils.getCurrentMonth());
        return jsonObject;
    }


}
