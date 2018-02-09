package pk.temp.bm.controller;

import com.amazonaws.util.json.JSONException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.util.json.JSONException;
import com.fasterxml.jackson.core.JsonProcessingException;
import pk.temp.bm.component.PermissionService;
import pk.temp.bm.component.RoleServices;
import pk.temp.bm.component.UserService;
import pk.temp.bm.models.Permission;
import pk.temp.bm.models.Role;
import pk.temp.bm.models.User;
import pk.temp.bm.repositories.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    PermissionService permService;

    @Autowired
    UserService userService;

    @Autowired
    RoleServices roleService;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/getUsers", method = RequestMethod.POST)
    public JSONObject getUser()
            throws JsonProcessingException, ParseException {

        JSONObject response = userService.getUser();
        return response;

    }

    @RequestMapping(value="/getUserList" , method = RequestMethod.POST)
    public List<User> getUserList() {
        List<User> userList = (List<User>) userRepository.findAll();
        return userList;
    }
    @RequestMapping(value = "/createRole", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> createRole(@RequestParam("name") String name, @RequestBody List<Permission> permission) {

        Role role = roleService.createRoleWithPermissions(name, permission);
        return new ResponseEntity<>(role, HttpStatus.CREATED);

    }

    @RequestMapping(value = "/createUser", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestParam("id") String id,@RequestParam("username") String email, @RequestParam("fullName") String name,
                                        @RequestParam("phone") String phone, @RequestParam("password") String password,
                                        @RequestParam("enabled") Boolean enabled) {

        User user = userService.createUser(id,email, name, password, phone, enabled);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);

    }
}
