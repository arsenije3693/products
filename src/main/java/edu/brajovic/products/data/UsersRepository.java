package edu.brajovic.products.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.brajovic.products.models.UserEntity;

@Repository
public interface UsersRepository extends CrudRepository<UserEntity, Integer> {
UserEntity findByUsername(String username);
    
}
