package org.tm_msaligner.parallel.thread;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.tm_msaligner.parallel.task.ParallelTask;
import org.tm_msaligner.parallel.algorithm.AsynchronousParallelAlgorithm;

public abstract class Master implements AsynchronousParallelAlgorithm {
  protected int numberOfCores;
  protected BlockingQueue<ParallelTask> completedTaskQueue;
  protected BlockingQueue<ParallelTask> pendingTaskQueue;

  public Master(int numberOfCores) {
    this.numberOfCores = numberOfCores;
    this.completedTaskQueue =  new LinkedBlockingQueue<>();
    this.pendingTaskQueue = new LinkedBlockingQueue<>();
  }

  @Override
  public void submitInitialTasks(List<ParallelTask> initialTasks) {
    if (initialTasks.size() >= numberOfCores) {
      initialTasks.forEach(this::submitTask);
    } else {
      int idleWorkers = numberOfCores - initialTasks.size();
      initialTasks.forEach(this::submitTask);
      while (idleWorkers > 0) {
        submitTask(createNewTask());
        idleWorkers--;
      }
    }
  }

  @Override
  public ParallelTask waitForComputedTask() {
    ParallelTask evaluatedTask = null;
    try {
      evaluatedTask = completedTaskQueue.take();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return evaluatedTask;
  }

  @Override
  public abstract void processComputedTask(ParallelTask task);

  @Override
  public void submitTask(ParallelTask task) {
    pendingTaskQueue.add(task);
  }

  @Override
  public abstract ParallelTask createNewTask();

  @Override
  public boolean thereAreInitialTasksPending(List<ParallelTask> initialTasks) {
    return initialTasks.size() > 0;
  }

  @Override
  public ParallelTask getInitialTask(List<ParallelTask> initialTasks) {
    ParallelTask initialTask = initialTasks.get(0);
    initialTasks.remove(0);
    return initialTask;
  }

  @Override
  public abstract boolean stoppingConditionIsNotMet();

  public BlockingQueue<ParallelTask> getCompletedTaskQueue() {
    return completedTaskQueue;
  }

  public BlockingQueue<ParallelTask> getPendingTaskQueue() {
    return pendingTaskQueue;
  }
}
