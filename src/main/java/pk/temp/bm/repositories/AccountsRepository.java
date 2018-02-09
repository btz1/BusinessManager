package pk.temp.bm.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pk.temp.bm.models.AccountsModel;

/**
 * Created by Tanzeel on 10/2/2018.
 */

@Repository
public interface AccountsRepository extends CrudRepository<AccountsModel, Long> {

}
