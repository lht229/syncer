package com.github.zzt93.syncer.output;

import com.github.zzt93.syncer.Starter;
import com.github.zzt93.syncer.common.SyncData;
import com.github.zzt93.syncer.config.pipeline.output.PipelineOutput;
import com.github.zzt93.syncer.config.syncer.SyncerOutput;
import com.github.zzt93.syncer.input.connect.NamedThreadFactory;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.util.Assert;

/**
 * @author zzt
 */
public class OutputStarter implements Starter<PipelineOutput, List<OutputChannel>> {

  private static OutputStarter instance;
  private final OutputJob outputJob;
  private final ExecutorService service;
  private final ScheduledExecutorService batchService;
  private final int worker;

  private OutputStarter(PipelineOutput pipelineOutput, SyncerOutput module,
      BlockingQueue<SyncData> fromFilter) throws Exception {
    Assert.isTrue(module.getWorker() <= 8, "Too many worker thread");
    Assert.isTrue(module.getWorker() > 0, "Too few worker thread");
    service = Executors
        .newFixedThreadPool(module.getWorker(), new NamedThreadFactory("syncer-output"));
    batchService = Executors.newScheduledThreadPool(module.getBatch().getWorker(),
        new NamedThreadFactory("syncer-batch"));

    List<OutputChannel> outputChannels = fromPipelineConfig(pipelineOutput);
    outputJob = new OutputJob(fromFilter, outputChannels);
    worker = module.getWorker();
  }

  public static OutputStarter getInstance(PipelineOutput pipelineOutput, SyncerOutput syncer,
      BlockingQueue<SyncData> fromFilter) throws Exception {
    if (instance == null) {
      instance = new OutputStarter(pipelineOutput, syncer, fromFilter);
    }
    return instance;
  }

  public void start() throws InterruptedException {
    for (int i = 0; i < worker; i++) {
      service.submit(outputJob);
    }
  }

  @Override
  public List<OutputChannel> fromPipelineConfig(PipelineOutput pipelineOutput) throws Exception {
    return pipelineOutput.toOutputChannels();
  }
}