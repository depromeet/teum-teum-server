package net.teumteum.meeting.config;


import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

public class PageableHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_SIZE = "20";
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "id");
    private static final Integer MAX_SIZE = 50;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Pageable.class.equals(parameter.getParameterType());
    }

    @Override
    public @NotNull Mono<Object> resolveArgument(@NotNull MethodParameter methodParameter, @NotNull BindingContext bindingContext,
        ServerWebExchange serverWebExchange) {
        List<String> pageValues = serverWebExchange.getRequest().getQueryParams()
            .getOrDefault("page", List.of(DEFAULT_PAGE));
        List<String> sizeValues = serverWebExchange.getRequest().getQueryParams()
            .getOrDefault("size", List.of(DEFAULT_SIZE));

        String page = pageValues.get(0);

        String sortParam = serverWebExchange.getRequest().getQueryParams().getFirst("sort");
        Sort sort = DEFAULT_SORT;

        if (sortParam != null) {
            String[] parts = sortParam.split(",");
            if (parts.length == 2) {
                String property = parts[0];
                Sort.Direction direction = Sort.Direction.fromString(parts[1]);
                sort = Sort.by(direction, property);
            }
        }

        return Mono.just(
            PageRequest.of(
                Integer.parseInt(page),
                Math.min(Integer.parseInt(sizeValues.get(0)),
                    MAX_SIZE), sort
            )
        );
    }
}
