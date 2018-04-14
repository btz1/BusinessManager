package pk.temp.bm.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pk.temp.bm.models.ExpenseModel;

import java.util.Date;
import java.util.List;

/**
 * Created by Abubakar on 4/9/2018
 */
@Repository
public interface ExpenseRepository extends CrudRepository<ExpenseModel,Long>{

    List<ExpenseModel> findAllByDateBetween(Date startDate, Date endDate);

}
