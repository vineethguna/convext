package com.vineeth.serac.manager.pipeline;

import java.util.concurrent.CompletableFuture;

public interface IProcessor {
    CompletableFuture<ProcessorContext> process(ProcessorContext context);
}
