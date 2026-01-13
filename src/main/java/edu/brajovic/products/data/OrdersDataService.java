package edu.brajovic.products.data;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.brajovic.products.models.Mapper;
import edu.brajovic.products.models.OrderEntity;
import edu.brajovic.products.models.OrderModel;

@Service
public class OrdersDataService implements DataAccessInterface<OrderModel> {

    @Autowired
    private OrdersRepository ordersRepository;

    @Override
    public OrderModel getById(int id) {
        OrderEntity entity = ordersRepository.findById(id).orElse(null);
        return Mapper.toModel(entity);
    }

    @Override
    public Iterable<OrderModel> getAll() {
        ArrayList<OrderModel> models = new ArrayList<>();
        Iterable<OrderEntity> entities = ordersRepository.findAll();
        for (OrderEntity e : entities) {
            models.add(Mapper.toModel(e));
        }
        return models;
    }

    @Override
    public OrderModel create(OrderModel item) {
        OrderEntity saved = ordersRepository.save(Mapper.toEntity(item));
        return Mapper.toModel(saved);
    }

    @Override
    public OrderModel update(OrderModel item) {
        OrderEntity saved = ordersRepository.save(Mapper.toEntity(item));
        return Mapper.toModel(saved);
    }

    @Override
    public boolean deleteById(int id) {
        ordersRepository.deleteById(id);
        return true;
    }
}
