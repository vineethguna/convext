package com.vineeth.serac.manager.processors;

import java.util.concurrent.CompletionStage;

public class EvaluateFailureNodesProcessor implements IProcessor {
    private float quorumPercentage;

    public EvaluateFailureNodesProcessor() {
        this(0.5f);
    }

    public EvaluateFailureNodesProcessor(float quorumPercentage) {
        this.quorumPercentage = quorumPercentage;
    }

    @Override
    public CompletionStage<ProcessorContext> process(ProcessorContext context) {
        return null;
    }
}
