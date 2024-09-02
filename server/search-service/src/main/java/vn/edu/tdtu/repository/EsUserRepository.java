package vn.edu.tdtu.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.es.SyncUser;

import java.util.List;

@Repository
public interface EsUserRepository extends ElasticsearchRepository<SyncUser, String> {
    List<SyncUser> findByFullName(String fullName);
}
