package com.vineeth.serac.manager.processors;

import java.util.concurrent.CompletionStage;

public interface IProcessor {
    CompletionStage<ProcessorContext> process(ProcessorContext context);
}
