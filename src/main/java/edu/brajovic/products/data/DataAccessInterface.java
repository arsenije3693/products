package edu.brajovic.products.data;

public interface DataAccessInterface<T> {
    T getById(int id);
    Iterable<T> getAll();
    T create(T item);
    T update(T item);
    boolean deleteById(int id);
}
