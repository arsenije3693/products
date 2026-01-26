package edu.brajovic.products.data;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.brajovic.products.models.Mapper;
import edu.brajovic.products.models.UserEntity;
import edu.brajovic.products.models.UserModel;

@Service
public class UserDataService implements DataAccessInterface<UserModel>{
    @Autowired
    private UsersRepository usersRepository;

    @Override
    public UserModel getById(int id) {
        UserEntity entity = usersRepository.findById(id).orElse(null);
        return Mapper.toModel(entity);
    }

    public UserModel getByUsername(String username) {
        UserEntity entity = usersRepository.findByUsername(username);
        return Mapper.toModel(entity);
    }

    @Override
    public Iterable<UserModel> getAll() {
        ArrayList<UserModel> models = new ArrayList<>();
        Iterable<UserEntity> entities = usersRepository.findAll();
        for (UserEntity e : entities) {
            models.add(Mapper.toModel(e));
        }
        return models;
    }

    @Override
    public UserModel create(UserModel item) {
        UserEntity saved = usersRepository.save(Mapper.toEntity(item));
        return Mapper.toModel(saved);
    }

    @Override
    public UserModel update(UserModel item) {
        UserEntity saved = usersRepository.save(Mapper.toEntity(item));
        return Mapper.toModel(saved);
    }

    @Override
    public boolean deleteById(int id) {
        usersRepository.deleteById(id);
        return true;
    }

    
}
