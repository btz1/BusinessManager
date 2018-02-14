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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class);

    @RequestMapping(value = "/recordAttendance", method = RequestMethod.POST)
    public ResponseEntity<?> recordAttendance(@RequestBody String attendanceJSON) throws Exception {
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
            for(Object object : attendanceObject.entrySet()){
                Map.Entry entry = (Map.Entry)object;
                AttendanceModel attendanceModel = new AttendanceModel();
                EmployeeModel employeeModel = new EmployeeModel();
                employeeModel.setId(Long.valueOf(entry.getKey().toString()));
                attendanceModel.setEmployee(employeeModel);
                attendanceModel.setPresent(Boolean.valueOf(entry.getValue().toString()));
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