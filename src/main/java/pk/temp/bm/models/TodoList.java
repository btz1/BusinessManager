package pk.temp.bm.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.dao.DataAccessException;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "todo_list")
public class TodoList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Long id;

    @Column(name = "message")
    private String todoMessage;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name="date", nullable = false)
    private Date messageDate;

    @Column(name = "is_done",nullable=false)
    private boolean isDone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTodoMessage() {
        return todoMessage;
    }

    public void setTodoMessage(String todoMessage) {
        this.todoMessage = todoMessage;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        this.isDone = done;
    }

    public Date getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }
}
