package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.NewsView;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface NewsViewRepository extends PagingAndSortingRepository<NewsView, Long> {
}
