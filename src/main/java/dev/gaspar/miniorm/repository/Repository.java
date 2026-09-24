package dev.gaspar.miniorm.repository;

public interface Repository<T, ID> {
  public T findById(ID id);
}
