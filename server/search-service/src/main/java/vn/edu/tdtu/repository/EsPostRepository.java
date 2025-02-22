package vn.edu.tdtu.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.es.SyncPost;

import java.util.List;

@Repository
public interface EsPostRepository extends ElasticsearchRepository<SyncPost, String> {
}
