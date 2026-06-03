package at.rtr.rmbt.repository;


import at.rtr.rmbt.model.QoeClassification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Qoe classification repository interface.
 */
public interface QoeClassificationRepository extends JpaRepository<QoeClassification, Long> {
}
