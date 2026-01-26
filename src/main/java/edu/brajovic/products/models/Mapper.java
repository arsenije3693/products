package edu.brajovic.products.models;

public class Mapper {

    public static OrderModel toModel(OrderEntity entity) {
        if (entity == null) return null;
        return new OrderModel(
                entity.getId(),
                entity.getOrder_number(),
                entity.getProduct_name(),
                entity.getPrice(),
                entity.getQuantity()
        );
    }

    public static OrderEntity toEntity(OrderModel model) {
        if (model == null) return null;
        return new OrderEntity(
                model.getId(),
                model.getOrder_number(),
                model.getProduct_name(),
                model.getPrice(),
                model.getQuantity()
        );
    }

    public static UserModel toModel(UserEntity entity) {
        if (entity == null) return null;
        return new UserModel(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getRole(),
                entity.isEnabled()
        );
    }

    public static UserEntity toEntity(UserModel model) {
        if (model == null) return null;
        return new UserEntity(
                model.getId(),
                model.getUsername(),
                model.getPassword(),
                model.getRole(),
                model.isEnabled()
        );
    }
}
