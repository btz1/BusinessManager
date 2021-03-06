package pk.temp.bm.controller;

import jssc.SerialPortList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pk.temp.bm.models.TodoList;
import pk.temp.bm.services.TodoListService;
import pk.temp.bm.utilities.SmsSender;

import java.util.List;

@RestController
public class TodoController {

    @Autowired
    private TodoListService todoListService;

    @RequestMapping(value = "/saveTodoMessage", method = RequestMethod.POST)
    public void saveTodoMessage(@RequestParam("message") String todoMessage , @RequestParam(value = "isDone", required = false)
                                Boolean isDone ){
       todoListService.saveTodoMessage(todoMessage,isDone);
    }

    @RequestMapping(value = "/getAllTodoMessage", method = RequestMethod.GET)
    public List<TodoList> getTodoMessageWithDate() {
        SmsSender smsSender = new SmsSender();
        String smsCenter = "300000042";
        String phoneNumber = "3219988009";
        String msg = "testing sms from computer";

        String[] names = SerialPortList.getPortNames();

        return todoListService.getAllTodoMessages();
    }

}
