package com.ik.urlshortener.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.ik.urlshortener.model.UrlMapping;

/**
 * This Spring bean will be responsible for accessing and storing Data 
 */

@Repository
public interface UrlMappingRepository extends CrudRepository<UrlMapping, String> {
}
