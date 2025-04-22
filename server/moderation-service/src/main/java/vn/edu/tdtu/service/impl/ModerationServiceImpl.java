package vn.edu.tdtu.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.BulkModerationResponse;
import vn.edu.tdtu.dto.ModerateResponseDto;
import vn.edu.tdtu.dto.ModerationResponse;
import vn.edu.tdtu.dto.RejectReason;
import vn.edu.tdtu.message.ModerateMessage;
import vn.edu.tdtu.service.interfaces.GeminiService;
import vn.edu.tdtu.service.interfaces.ModerationService;
import vn.edu.tdtu.utils.BeanOutputParser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModerationServiceImpl implements ModerationService {
    @Value("${sight.engine.api.key.user}")
    private String userKey;
    @Value("${sight.engine.api.key.secret}")
    private String secretKey;
    @Value("${sight.engine.get.url}")
    private String requestUrl;
    @Value("${sight.engine.workflow}")
    private String workflow;
    @Value("classpath:/prompts/moderation.st")
    private Resource moderationResource;
    @Value("classpath:/prompts/reason_parsing.st")
    private Resource reasonParsingResource;
    private final GeminiService geminiService;

    @Override
    public ModerateResponseDto moderateImage(String mediaUrl) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URI uri = buildUri(mediaUrl);
            HttpGet httpGet = new HttpGet(uri);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                return handleResponse(response.getStatusLine().getStatusCode(), responseBody);
            }
        } catch (IOException | URISyntaxException e) {
            log.error("Error during moderation: {}", e.getMessage(), e);
        }

        return ModerateResponseDto.builder()
                .accept(false)
                .rejectReason("Error during moderation")
                .build();
    }

    public ModerateResponseDto bulkModerateImages(List<String> mediaUrls) {
        List<List<String>> urlsPartitioned = mediaUrlsPartitioned(mediaUrls);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(requestUrl);
            StringBuilder rejectBuilder = new StringBuilder();
            boolean accept = true;

            for (List<String> urls : urlsPartitioned) {
                ModerateResponseDto responseDto = bulkModerating(urls, httpPost, httpClient);

                if (!responseDto.isAccept()) {
                    rejectBuilder
                            .append(responseDto.getRejectReason())
                            .append("; ");

                    if (accept) {
                        accept = false;
                    }
                }
            }

            handleRejectMessage(rejectBuilder);

            return ModerateResponseDto.builder()
                    .accept(accept)
                    .rejectReason(rejectBuilder.toString())
                    .build();

        } catch (Exception e) {
            log.error("Error during moderation: {}", e.getMessage(), e);
        }

        return ModerateResponseDto.builder()
                .accept(false)
                .rejectReason("Error during moderation")
                .build();
    }

    @Override
    public ModerateResponseDto moderateText(String content) {
        BeanOutputParser<ModerateResponseDto> outputParser = new BeanOutputParser<>();

        String response = geminiService.call(moderationResource, Map.of(
                "content",
                content,
                "format",
                outputParser.getFormat(ModerateResponseDto.class)
        ));

        log.info(response);

        ModerateResponseDto responseDto = outputParser.parse(response, ModerateResponseDto.class);

        return Objects.isNull(responseDto) ? moderateText(content) : responseDto;
    }

    @Override
    public ModerateResponseDto moderate(ModerateMessage message) {
        CompletableFuture<ModerateResponseDto> imagesModerate = CompletableFuture.supplyAsync(() -> {
            return bulkModerateImages(message.getImageUrls());
        });

        CompletableFuture<ModerateResponseDto> contentModerate = CompletableFuture.supplyAsync(() -> {
            return moderateText(message.getContent());
        });

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(imagesModerate, contentModerate);

        CompletableFuture<ModerateResponseDto> moderateResult = allTasks.thenApply((ignored) -> {
            ModerateResponseDto imageResult = imagesModerate.join();
            ModerateResponseDto contentResult = contentModerate.join();

            boolean isAccepted = imageResult.isAccept() && contentResult.isAccept();
            String rejectReasons = Stream.of(imageResult.getRejectReason(), contentResult.getRejectReason())
                    .filter(reason -> reason != null && !reason.isBlank())
                    .collect(Collectors.joining("; "));

            if (!isAccepted) {
                rejectReasons = getVietnameseRejectReason(rejectReasons);
            }

            return new ModerateResponseDto(isAccepted, rejectReasons.isEmpty() ? null : rejectReasons);
        });

        return moderateResult.join();
    }

    private List<List<String>> mediaUrlsPartitioned(List<String> mediaUrls) {
        List<List<String>> partitions = new ArrayList<>();
        for (int i = 0; i < mediaUrls.size(); i += 6) {
            partitions.add(mediaUrls.subList(i, Math.min(i + 6, mediaUrls.size())));
        }
        return partitions;
    }

    private ModerateResponseDto bulkModerating(List<String> mediaUrls, HttpPost httpPost, CloseableHttpClient httpClient) throws IOException {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        mediaUrls.forEach(mediaUrl -> {
            builder.addTextBody("url[]", mediaUrl, ContentType.TEXT_PLAIN);
        });

        builder.addTextBody("workflow", workflow, ContentType.TEXT_PLAIN);
        builder.addTextBody("api_user", userKey, ContentType.TEXT_PLAIN);
        builder.addTextBody("api_secret", secretKey, ContentType.TEXT_PLAIN);

        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            String responseString = EntityUtils.toString(response.getEntity());

            return handleBulkResponse(response.getStatusLine().getStatusCode(), responseString);
        }
    }

    private URI buildUri(String mediaUrl) throws URISyntaxException {
        return new URI(String.format("%s?url=%s&workflow=%s&api_user=%s&api_secret=%s",
                requestUrl, mediaUrl, workflow, userKey, secretKey));
    }

    private ModerateResponseDto handleResponse(int statusCode, String responseBody) throws IOException {
        if (statusCode == 200) {
            ObjectMapper mapper = new ObjectMapper();
            ModerationResponse moderationResponse = mapper.readValue(responseBody, ModerationResponse.class);
            boolean result = "accept".equalsIgnoreCase(moderationResponse.getSummary().getAction());

            return ModerateResponseDto.builder()
                    .accept(result)
                    .rejectReason(
                            result ? "" : moderationResponse.getSummary().getReject_reason()
                                    .stream()
                                    .map(RejectReason::getText)
                                    .collect(Collectors.joining("; "))
                    )
                    .build();
        } else {
            log.info("Response: {} | Status code: {}", responseBody, statusCode);
        }

        return ModerateResponseDto.builder()
                .accept(false)
                .rejectReason("Error during moderation")
                .build();
    }

    private ModerateResponseDto handleBulkResponse(int statusCode, String responseBody) throws IOException {
        if (statusCode == 200) {
            ObjectMapper mapper = new ObjectMapper();
            BulkModerationResponse moderationResponse = mapper.readValue(responseBody, BulkModerationResponse.class);

            if (moderationResponse.getData() != null) {
                return handleBulkResponseData(moderationResponse);
            }

            return handleResponse(statusCode, responseBody);
        }

        log.info("Response: {} | Status code: {}", responseBody, statusCode);

        return ModerateResponseDto.builder()
                .accept(false)
                .rejectReason("Error during moderation")
                .build();
    }

    private static ModerateResponseDto handleBulkResponseData(BulkModerationResponse moderationResponse) {
        boolean result = true;
        StringBuilder rejectReason = new StringBuilder();

        for (ModerationResponse data : moderationResponse.getData()) {
            if ("reject".equalsIgnoreCase(data.getSummary().getAction())) {
                result = false;
                rejectReason.append(data.getSummary().getReject_reason()
                                .stream()
                                .map(RejectReason::getText)
                                .collect(Collectors.joining("; ")))
                        .append("; ");
            }
        }

        handleRejectMessage(rejectReason);

        return ModerateResponseDto.builder()
                .accept(result)
                .rejectReason(rejectReason.toString())
                .build();
    }

    private static void handleRejectMessage(StringBuilder rejectReason) {
        if (!rejectReason.isEmpty())
            rejectReason.delete(rejectReason.length() - 2, rejectReason.length());
    }

    private String getVietnameseRejectReason(String englishRejectReason) {
        String response = geminiService.call(reasonParsingResource, Map.of(
                "reason",
                englishRejectReason
        ));

        log.info("Vietnamese reject reason: " + response);

        return response;
    }
}
