package vn.tdtu.common.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.utils.RequestUtils;
import vn.tdtu.common.utils.SecurityContextUtils;

@RequiredArgsConstructor
public class FeignRequestInterceptor implements RequestInterceptor {
    private final RequestUtils requestUtils;
    private final Logger logger = LoggerFactory.getLogger(FeignRequestInterceptor.class);

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String internalSecretToken = requestUtils.getInternalSecretToken();
        String userId = SecurityContextUtils.getUserId();

        logger.info("[FeignRequestInterceptor] setting template headers: [{}: {}, {}: {}] for request: {}",
                Constants.RequestHeader.X_FEIGN_USER_ID, userId,
                Constants.RequestHeader.X_FEIGN_INTERNAL_HEADER, internalSecretToken,
                requestTemplate.path());

        requestTemplate.header(Constants.RequestHeader.X_FEIGN_USER_ID, userId);
        requestTemplate.header(Constants.RequestHeader.X_FEIGN_INTERNAL_HEADER, internalSecretToken);
    }
}
