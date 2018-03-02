package pk.temp.bm.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pk.temp.bm.models.TodoList;

@Repository
public interface TodoListRepository extends CrudRepository<TodoList, Long>{

}
