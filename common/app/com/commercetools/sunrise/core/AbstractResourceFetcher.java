package com.commercetools.sunrise.core;

import com.commercetools.sunrise.core.hooks.HookRunner;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereRequest;
import io.sphere.sdk.queries.PagedResult;
import play.libs.concurrent.HttpExecution;

import java.util.concurrent.CompletionStage;

public abstract class AbstractResourceFetcher<T, R extends SphereRequest<P>, P extends PagedResult<T>> extends AbstractSphereRequestExecutor implements ResourceFetcher<P, R> {

    protected AbstractResourceFetcher(final SphereClient sphereClient, final HookRunner hookRunner) {
        super(sphereClient, hookRunner);
    }

    @Override
    public CompletionStage<P> get(final R request) {
        return executeRequest(request);
    }

    protected final CompletionStage<P> executeRequest(final R baseRequest) {
        return runRequestHook(getHookRunner(), baseRequest).thenCompose(request -> {
            final CompletionStage<P> resultStage = getSphereClient().execute(request);
            resultStage.thenAcceptAsync(result -> runResourceLoadedHook(getHookRunner(), result), HttpExecution.defaultContext());
            return resultStage;
        });
    }

    protected abstract CompletionStage<R> runRequestHook(HookRunner hookRunner, R baseRequest);

    protected abstract void runResourceLoadedHook(HookRunner hookRunner, P pagedResult);
}
