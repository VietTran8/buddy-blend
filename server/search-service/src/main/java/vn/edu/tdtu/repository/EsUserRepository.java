package vn.edu.tdtu.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.es.SyncUser;

@Repository
public interface EsUserRepository extends ElasticsearchRepository<SyncUser, String> {
}
