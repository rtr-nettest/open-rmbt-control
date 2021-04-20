package at.rtr.rmbt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.UUID;

@NoRepositoryBean
public interface CustomRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    void refresh(T t);

    Integer updateImplausible(boolean implausible, String comment, String uuidField, UUID uuid);
}
