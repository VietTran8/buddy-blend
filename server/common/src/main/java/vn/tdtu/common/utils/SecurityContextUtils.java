package vn.tdtu.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import vn.tdtu.common.enums.user.EUserRole;
import vn.tdtu.common.exception.UnauthorizedException;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class SecurityContextUtils {
    private static final Logger logger = LoggerFactory.getLogger(SecurityContextUtils.class);
    private static final ExecutorService securedExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4,
            runnable -> {
                Thread thread = new SecuredWorkerThread(runnable);
                thread.setDaemon(true);
                return thread;
            });

    public static String getUserId() {
        logger.info("[getUserId] - Current processing thread: {}", Thread.currentThread().getName());
        Optional<Authentication> authentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());

        if (authentication.isPresent() && authentication.get().getPrincipal() instanceof Jwt jwt) {
            Map<String, Object> claims = jwt.getClaims();
            Object userIdClaim = claims.get(Constants.JwtClaims.USER_ID);

            if (userIdClaim == null) {
                logger.warn("[getUserId] - user_id claim is null in JWT: {}", jwt);
                throw new UnauthorizedException("user_id claim is missing in the JWT");
            }

            logger.info("[getUserId] - Retrieved user_id claim: {}", userIdClaim);

            return userIdClaim.toString();
        }

        logger.warn("[getUserId] - Failed to retrieve user id claim with authentication context: {}", SecurityContextHolder.getContext().getAuthentication());

        return null;
    }

    public static void byPassAuthentication(HttpServletRequest request) {
        Optional<Authentication> existedAuthentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
        String userIdHeader = request.getHeader(Constants.RequestHeader.X_FEIGN_USER_ID);

        if(existedAuthentication.isEmpty() && StringUtils.isNotBlank(userIdHeader)) {
            Authentication authentication = getAuthentication(userIdHeader);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private static Authentication getAuthentication(String userIdHeader) {
        Map<String, Object> dummyHeader = Map.of(
                "alg", "HS256",
                "typ", "JWT"
        );

        Map<String, Object> claims = Map.of(
                Constants.JwtClaims.USER_ID,
                userIdHeader
        );

        Jwt jwt = new Jwt("feign-call-token-value", null, null,
                dummyHeader,
                claims
        );

        Authentication authentication = new JwtAuthenticationToken(jwt);
        authentication.setAuthenticated(true);
        return authentication;
    }

    public static List<EUserRole> getUserRoles() {
        Optional<Authentication> authentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());

        if (authentication.isPresent() && authentication.get().getPrincipal() instanceof Jwt jwt) {
            Object userIdClaim = jwt.getClaims().get(Constants.JwtClaims.USER_ID);
            List<String> userRoles = jwt.getClaimAsStringList(Constants.JwtClaims.USER_ROLES);

            logger.info("[getUserRoles] - Retrieved user [{}] with roles: {}",
                    userIdClaim,
                    StringUtils.join("[", String.join(", ", userRoles), "]")
            );

            return userRoles.stream()
                    .filter(EUserRole::hasRole)
                    .map(EUserRole::valueOf)
                    .toList();
        }

        logger.warn("[getUserRoles] - Failed to retrieve user roles with authentication context: {}", SecurityContextHolder.getContext().getAuthentication());

        return Collections.emptyList();
    }

    private static Optional<Authentication> getCurrentAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            logger.warn("[processHeavyListInSecuredContext][getAuthentication] - No authentication found in the security context.");
        } else {
            logger.info("[processHeavyListInSecuredContext][getAuthentication] - Retrieved authentication: {}", authentication);
        }
        return Optional.ofNullable(authentication);
    }

    private static void delegateContext(Authentication auth) {
        boolean isSecuredWorker = isSecuredWorker();

        if(isSecuredWorker) {
            logger.info("[processHeavyListInSecuredContext][runWithAuthentication][delegateContext] - Delegating security context for thread: {}", Thread.currentThread().getName());

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
        }
    }

    private static void clearContext() {
        if(isSecuredWorker()) {
            logger.info("[processHeavyListInSecuredContext][runWithAuthentication][clearContext] - Clearing security context for thread: {}", Thread.currentThread().getName());
            SecurityContextHolder.clearContext();
        } else {
            logger.warn("[processHeavyListInSecuredContext][runWithAuthentication][clearContext] - Attempted to clear security context on a non-secured worker thread: {}", Thread.currentThread().getName());
        }
    }

    private static boolean isSecuredWorker() {
        return Thread.currentThread() instanceof SecuredWorkerThread;
    }

    private static <T> CompletableFuture<T> runWithAuthentication(Authentication auth, Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {;
            delegateContext(auth);
            try {
                return supplier.get();
            } finally {
                clearContext();
            }
        }, securedExecutor);
    }

    /**
     * Processes a list of items in a secured context using the provided supplier.
     * @param list the list of items to process
     * @param runner the runner that provides the processing logic for each item
     * @return a list of results from concurrent processing each item in the secured context
     * @param <T> the type of items in the list to process
     * @param <R> the type of results produced by the supplier
     */
    public static <T, R> List<R> heavilyProcessListInSecuredContext(List<T> list, SecurityContextRunnable<T, R> runner) {
        long startTime = System.currentTimeMillis();

        Optional<Authentication> optionalAuth = getCurrentAuthentication();

        List<CompletableFuture<R>> completableFutures = list.stream().map(
                item -> optionalAuth.map(
                            auth -> runWithAuthentication(auth, () -> runner.get(item))
                        ).orElse(null)
        ).toList();

        CompletableFuture<Void> allDone = CompletableFuture.allOf(
                completableFutures.toArray(new CompletableFuture[0])
        );

        //Waiting for all posts processed
        allDone.join();

        logger.info("[processHeavyListInSecuredContext] - Finished processing list in {} ms", System.currentTimeMillis() - startTime);

        return completableFutures.stream().filter(Objects::nonNull).map(CompletableFuture::join)
                .toList();
    }

    public interface SecurityContextRunnable<T, R> {
        R get(T input);
    }

    public static class SecuredWorkerThread extends Thread {
        public SecuredWorkerThread(Runnable r) {
            super(r);
            this.setName(Constants.SECURED_WORKER_NAME_PREFIX + getId());
        }
    }
}