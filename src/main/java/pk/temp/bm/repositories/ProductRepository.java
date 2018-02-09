package pk.temp.bm.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import pk.temp.bm.models.ProductModel;


@Repository
public interface ProductRepository  extends PagingAndSortingRepository<ProductModel, Long> {


}


