package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.QosTestObjective;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface QosTestObjectiveRepository extends JpaRepository<QosTestObjective, Long> {

    List<QosTestObjective> getByTestClassIdIn(Collection<Integer> testClassesIds);
}
