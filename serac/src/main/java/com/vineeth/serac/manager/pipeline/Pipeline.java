package com.vineeth.serac.manager.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Pipeline {
    private List<IProcessor> processorList;

    public Pipeline() {
        processorList = new ArrayList<>();
    }

    public void add(IProcessor processor) {
        processorList.add(processor);
    }

    public void start() {
        ProcessorContext processorContext = new ProcessorContext();
        if(processorList.size() > 0) {
            IProcessor firstProcessor = processorList.get(0);
            CompletableFuture<ProcessorContext> currentCompletionStage = firstProcessor.process(processorContext);
            for(int i = 1; i < processorList.size(); i++) {
                IProcessor currentProcessor = processorList.get(i);
                currentCompletionStage = currentCompletionStage.thenComposeAsync(currentProcessor::process);
            }
        }
    }
}
