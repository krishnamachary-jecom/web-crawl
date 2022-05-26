package com.dbs.web.crawler.repository;

import com.dbs.web.crawler.model.WebCrawlDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

public interface WebCrawlRepository extends SolrCrudRepository<WebCrawlDetails, String> {

    @Query("title:*?0* OR keyword:*?0* OR description:*?0* OR linkedUrls:*?0*")
    public Page<WebCrawlDetails> findByCustomQuery(String searchTerm, Pageable pageable);
}
