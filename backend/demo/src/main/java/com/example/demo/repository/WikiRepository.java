package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.WikiPage;

/**
 * Repository interface for managing wiki page entities.
 * <p>
 * Provides CRUD operations for {@link WikiPage} entities.
 * </p>
 *
 */

@Repository
public interface WikiRepository extends CrudRepository<WikiPage, Long> {

}
