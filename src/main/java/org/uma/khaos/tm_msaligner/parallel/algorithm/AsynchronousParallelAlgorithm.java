package org.uma.khaos.tm_msaligner.parallel.algorithm;

import java.util.List;
import org.uma.khaos.tm_msaligner.parallel.task.ParallelTask;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;

/**
 * Abstract class representing asynchronous parallel algorithms. The general idea is that whenever a new {@link ParallelTask}
 * object is created, it is sent to an external entity (e.g., a thread) to be computed in an asynchronous way.
 * It is assumed that there is an initial task list (which could be empty). The main loop of the algorithms consists
 * in waiting for any computed task which and, when one is received, it is processed and then a new task can be created
 * and submitted to be computed. The speed-ups that can be obtained will depend on the number of external entities,
 * the granularity of the task computation, and time required to process a received computed task.
 */
public interface AsynchronousParallelAlgorithm {
  void submitInitialTasks(List<ParallelTask> tasks);
  List<ParallelTask> createInitialTasks() ;
  ParallelTask waitForComputedTask();
  void processComputedTask(ParallelTask task);
  void submitTask(ParallelTask task);
  ParallelTask createNewTask();
  boolean thereAreInitialTasksPending(List<ParallelTask> tasks);
  ParallelTask getInitialTask(List<ParallelTask> tasks);
  boolean stoppingConditionIsNotMet();
  void initProgress() ;
  void updateProgress() ;
  List<TM_MSASolution> getResult() ;

  default void run() {
    List<ParallelTask> initialTasks = createInitialTasks();
    submitInitialTasks(initialTasks);

    initProgress() ;
    while (stoppingConditionIsNotMet()) {
      ParallelTask computedTask = waitForComputedTask();
      processComputedTask(computedTask);

      if (thereAreInitialTasksPending(initialTasks)) {
        submitTask(getInitialTask(initialTasks));
      } else {
        submitTask(createNewTask());
      }
      updateProgress();
    }
  }
}
