package pk.temp.bm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pk.temp.bm.models.AttendanceModel;
import pk.temp.bm.models.EmployeeModel;
import pk.temp.bm.repositories.AttendanceRepository;

import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    public void markAttendance(List<AttendanceModel> attendanceModelList){
        attendanceRepository.save(attendanceModelList);
    }

    public List<AttendanceModel> getEmployeeAttendance(Long employeeId){
        EmployeeModel employeeModel = new EmployeeModel();
        employeeModel.setId(employeeId);
        return attendanceRepository.findByEmployee(employeeModel);
    }
    public List<AttendanceModel> getAllAttendance(){
        return (List<AttendanceModel>) attendanceRepository.findAll();
    }

}
