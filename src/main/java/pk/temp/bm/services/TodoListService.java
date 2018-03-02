package pk.temp.bm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pk.temp.bm.models.ProductModel;
import pk.temp.bm.models.TodoList;
import pk.temp.bm.repositories.TodoListRepository;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.DataFormatException;

@Service
public class TodoListService {

    @Autowired
    private TodoListRepository todoListRepository;

    public void saveTodoMessage(String message,Boolean isdone){

        TodoList todoListMessage = new TodoList();
            Date date = new Date();
            String date1 = date.toString();
            todoListMessage.setTodoMessage(message);
                todoListMessage.setMessageDate(date1);
//            todoListMessage.setEnabled(isdone);

        todoListRepository.save(todoListMessage);
    }

    public List<TodoList> getAllTodoMessages(){

            return (List<TodoList>) todoListRepository.findAll();
        }
    }

