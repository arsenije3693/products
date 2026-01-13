package edu.brajovic.products.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import edu.brajovic.products.models.OrderEntity;

@Repository
public interface OrdersRepository extends CrudRepository<OrderEntity, Integer> {
}
