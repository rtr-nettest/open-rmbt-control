package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.NewsView;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * News view repository interface.
 */
public interface NewsViewRepository extends PagingAndSortingRepository<NewsView, Long>, CrudRepository<NewsView, Long> {
}
