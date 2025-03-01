package vn.edu.tdtu.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.es.SyncGroup;

@Repository
public interface EsGroupRepository extends ElasticsearchRepository<SyncGroup, String> {
}
