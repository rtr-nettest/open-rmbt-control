package at.rtr.rmbt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.UUID;

/**
 * Custom repository interface.
 */
@NoRepositoryBean
public interface CustomRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    /**
     * Refresh.
     *
     * @param t the T
     */
    void refresh(T t);

    /**
     * Update implausible.
     *
     * @param implausible the Implausible
     * @param comment the Comment
     * @param uuidField the Uuid field
     * @param uuid the Uuid
     * @return the result
     */
    Integer updateImplausible(boolean implausible, String comment, String uuidField, UUID uuid);
}
