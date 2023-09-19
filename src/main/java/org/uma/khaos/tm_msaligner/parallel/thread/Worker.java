package org.uma.khaos.tm_msaligner.parallel.thread;

import java.util.concurrent.BlockingQueue;
import java.util.function.Function;
import org.uma.khaos.tm_msaligner.parallel.task.ParallelTask;

public class Worker extends Thread {
  private BlockingQueue<ParallelTask> completedTaskQueue;
  private BlockingQueue<ParallelTask> pendingTaskQueue;
  protected Function<ParallelTask, ParallelTask> computeFunction;

  public Worker(
      Function<ParallelTask, ParallelTask> computeFunction,
      BlockingQueue<ParallelTask> pendingTaskQueue,
      BlockingQueue<ParallelTask> completedTaskQueue) {
    this.computeFunction = computeFunction;
    this.completedTaskQueue = completedTaskQueue;
    this.pendingTaskQueue = pendingTaskQueue;
  }

  @Override
  public void run() {
    while (true) {
      ParallelTask taskToCompute = null;

      try {
        taskToCompute = pendingTaskQueue.take();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      ParallelTask computedTask = computeFunction.apply(taskToCompute);

      completedTaskQueue.add(computedTask);
    }
  }

  public BlockingQueue<ParallelTask> getCompletedTaskQueue() {
    return completedTaskQueue;
  }

  public BlockingQueue<ParallelTask> getPendingTaskQueue() {
    return pendingTaskQueue;
  }
}
