package pk.temp.bm.controller;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pk.temp.bm.models.AttendanceModel;
import pk.temp.bm.models.EmployeeModel;
import pk.temp.bm.services.AttendanceService;
import pk.temp.bm.services.EmployeeService;

import java.util.*;

@RestController
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private EmployeeService employeeService;

    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class);

    @RequestMapping(value = "/recordAttendance", method = RequestMethod.POST)
    public ResponseEntity<?> recordAttendance(@RequestParam("attendanceJSON") String attendanceJSON) throws Exception {
        /* Attendance JSON format
        *
        * {employeeId,true/false}
        *
        * */
        Boolean attendanceMarked = false;
        try{
            JSONParser parser = new JSONParser();
            JSONObject attendanceObject = (JSONObject) parser.parse(attendanceJSON);
            List<AttendanceModel> attendanceModelList = new ArrayList<>();
            Date currentDate = new Date();
            Map<String,Object> attendanceMap = new HashMap<>();
            for(Object object : attendanceObject.entrySet()) {
                Map.Entry entry = (Map.Entry) object;
                attendanceMap.put((String) entry.getKey(),entry.getValue());
            }
                List<EmployeeModel> existingEmployees = employeeService.getActiveEmployees();
            for(EmployeeModel employeeModel : existingEmployees){
                Boolean present = true;
                if(attendanceMap.containsKey(employeeModel.getId())){
                    present = (Boolean) attendanceMap.get(employeeModel.getId());
                }
                AttendanceModel attendanceModel = new AttendanceModel();
                attendanceModel.setEmployee(employeeModel);
                attendanceModel.setPresent(present);
                attendanceModel.setDate(currentDate);
                attendanceModelList.add(attendanceModel);
            }
            attendanceService.markAttendance(attendanceModelList);
            attendanceMarked = true;
        }
        catch (Exception e){
            logger.error("Exception while marking attendance : "+e.getMessage(),e);
        }
        return new ResponseEntity<Object>(attendanceMarked ? HttpStatus.ACCEPTED : HttpStatus.NOT_ACCEPTABLE);
    }

    @RequestMapping(value = "/getEmployeeAttendance")
    public List<AttendanceModel> getEmployeeAttendance(@RequestParam("employeeId") Long employeeId){
        return attendanceService.getEmployeeAttendance(employeeId);
    }

}
