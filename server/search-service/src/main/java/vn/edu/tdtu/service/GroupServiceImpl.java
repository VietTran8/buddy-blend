package vn.edu.tdtu.service;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.model.es.SyncGroup;
import vn.edu.tdtu.repository.EsGroupRepository;
import vn.edu.tdtu.repository.httpclient.GroupClient;
import vn.edu.tdtu.service.interfaces.GroupService;
import vn.tdtu.common.dto.GroupDTO;
import vn.tdtu.common.exception.BadRequestException;
import vn.tdtu.common.utils.MessageCode;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final GroupClient groupClient;
    private final EsGroupRepository groupRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void saveGroup(SyncGroup group) {
        groupRepository.save(group);
    }

    @Override
    public void updateGroup(SyncGroup group) {
        groupRepository.findById(group.getId()).ifPresentOrElse(
                foundGroup -> {
                    foundGroup.setName(group.getName());
                    groupRepository.save(foundGroup);
                }, () -> {
                    throw new BadRequestException(MessageCode.Group.GROUP_NOT_FOUND_ID, group.getId());
                }
        );
    }

    @Override
    public void deleteGroup(String groupId) {
        groupRepository.deleteById(groupId);
    }

    @Override
    public List<GroupDTO> findByNameContaining(String key, String fuzziness) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.match(mq -> mq
                                .field("name")
                                .query(key)
                                .fuzziness(fuzziness)
                        )
                )
                .withSort(SortOptions.of(so -> so
                        .score(score -> score
                                .order(SortOrder.Desc)
                        )
                ))
                .build();

        SearchHits<SyncGroup> searchHits = elasticsearchOperations.search(query, SyncGroup.class);

        List<String> matchGroupIds = searchHits.getSearchHits()
                .stream()
                .map(hit -> hit.getContent().getId())
                .collect(Collectors.toList());

        return groupClient
                .getAllGroupByIds(matchGroupIds)
                .getData();
    }
}
