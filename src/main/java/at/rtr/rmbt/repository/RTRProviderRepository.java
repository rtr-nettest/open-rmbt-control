package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RTRProviderRepository extends JpaRepository<Provider, Long> {

    @Query(value = "SELECT * FROM public.rmbt_set_provider_from_as(:testId)", nativeQuery = true)
    String getProviderNameByTestId(Long testId);
}
