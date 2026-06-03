package at.rtr.rmbt.repository.impl;

import at.rtr.rmbt.repository.CustomRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.UUID;

/**
 * Custom repository impl class.
 */
public class CustomRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements CustomRepository<T, ID> {

    public static final String UPDATE_IMPLAUSIBLE = "UPDATE public.test SET implausible = :implausible, comment=:comment WHERE NOT implausible = :implausible AND deleted=FALSE AND uid IN ("
            + "SELECT t.uid FROM public.test t INNER JOIN client c ON t.client_id = c.uid WHERE %s = :uuid)";

    private final EntityManager entityManager;

    /**
     * Creates a new CustomRepositoryImpl instance.
     *
     * @param entityInformation the Entity information
     * @param entityManager the Entity manager
     */
    public CustomRepositoryImpl(JpaEntityInformation entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    /**
     * Refresh.
     *
     * @param t the T
     */
    @Override
    @Transactional
    public void refresh(T t) {
        // Flush pending changes first so refresh() reloads DB-computed values WITHOUT discarding
        // the caller's own unflushed writes. Inside a single transaction (e.g. a @Transactional
        // result handler) a bare entityManager.refresh() would throw away in-memory changes that
        // were not yet flushed - previously those got flushed implicitly by per-call commits.
        entityManager.flush();
        entityManager.refresh(t);
    }

    /**
     * Update implausible.
     *
     * @param implausible the Implausible
     * @param comment the Comment
     * @param uuidField the Uuid field
     * @param uuid the Uuid
     * @return the result
     */
    @Override
    @Transactional
    public Integer updateImplausible(boolean implausible, String comment, String uuidField, UUID uuid) {
        Query query = entityManager.createNativeQuery(String.format(UPDATE_IMPLAUSIBLE, uuidField));
        query.setParameter("implausible", implausible)
                .setParameter("comment", comment)
                .setParameter("uuid", uuid);
        return query.executeUpdate();
    }
}
