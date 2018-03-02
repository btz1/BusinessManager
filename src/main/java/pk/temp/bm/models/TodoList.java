package pk.temp.bm.models;


import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;

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
    private String messageDate;

    @Column(name = "is_done",nullable=false)
    private boolean enabled;

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }
}
