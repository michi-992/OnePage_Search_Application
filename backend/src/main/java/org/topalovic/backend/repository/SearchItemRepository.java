package org.topalovic.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.topalovic.backend.model.SearchItem;

@Repository
public interface SearchItemRepository extends JpaRepository<SearchItem, Long> {
}
