package org.uma.khaos.tm_msaligner.parallel.algorithm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.impl.DefaultObservable;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.khaos.tm_msaligner.parallel.task.ParallelTask;
import org.uma.khaos.tm_msaligner.parallel.thread.Master;
import org.uma.khaos.tm_msaligner.parallel.thread.Worker;
import org.uma.khaos.tm_msaligner.problem.StandardTMMSAProblem;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;
import org.uma.khaos.tm_msaligner.solutionscreation.PreComputedMSAsSolutionsCreation;

public class AsynchronousMultiThreadedGeneticAlgorithm extends Master {
  private Problem<TM_MSASolution> problem;
  private CrossoverOperator<TM_MSASolution> crossover;
  private MutationOperator<TM_MSASolution> mutation;
  private SelectionOperator<List<TM_MSASolution>, TM_MSASolution> selection;
  private Replacement<TM_MSASolution> replacement;
  private Termination termination;

  private List<TM_MSASolution> population = new ArrayList<>();
  private int populationSize;
  private int evaluations = 0;
  private long initTime;

  private Map<String, Object> attributes;
  private Observable<Map<String, Object>> observable;

  private int numberOfCores;

  public AsynchronousMultiThreadedGeneticAlgorithm(
      int numberOfCores,
      Problem<TM_MSASolution> problem,
      int populationSize,
      CrossoverOperator<TM_MSASolution> crossover,
      MutationOperator<TM_MSASolution> mutation,
      SelectionOperator<List<TM_MSASolution>, TM_MSASolution> selection,
      Replacement<TM_MSASolution> replacement,
      Termination termination) {
    super(numberOfCores);
    this.problem = problem;
    this.crossover = crossover;
    this.mutation = mutation;
    this.populationSize = populationSize;
    this.termination = termination;
    this.selection = selection ;
    this.replacement = replacement ;

    attributes = new HashMap<>();
    observable = new DefaultObservable<>("Observable");

    this.numberOfCores = numberOfCores;

    createWorkers(numberOfCores, problem);
  }

  private void createWorkers(int numberOfCores, Problem<TM_MSASolution> problem) {
    IntStream.range(0, numberOfCores).forEach(i -> new Worker(
            (task) -> {
              problem.evaluate(task.getContents());
              return ParallelTask.create(createTaskIdentifier(), task.getContents());
            },
            pendingTaskQueue,
            completedTaskQueue).start());
  }

  private int createTaskIdentifier() {
    return JMetalRandom.getInstance().nextInt(0, 1000000000) ;
  }

  @Override
  public void initProgress() {
    attributes.put("EVALUATIONS", evaluations);
    attributes.put("POPULATION", population);
    attributes.put("COMPUTING_TIME", System.currentTimeMillis() - initTime);

    observable.setChanged();
    //observable.notifyObservers(attributes);
  }

  @Override
  public void updateProgress() {
    attributes.put("EVALUATIONS", evaluations);
    attributes.put("POPULATION", population);
    attributes.put("COMPUTING_TIME", System.currentTimeMillis() - initTime);
    attributes.put("BEST_SOLUTION", population.get(0));

    observable.setChanged();
    observable.notifyObservers(attributes);
  }

  @Override
  public List<ParallelTask> createInitialTasks() {
    List<TM_MSASolution> initialPopulation ;
    List<ParallelTask> initialTaskList = new ArrayList<>() ;
    initialPopulation = new PreComputedMSAsSolutionsCreation((StandardTMMSAProblem) problem, populationSize).create() ;
    initialPopulation.forEach(
        solution -> {
          int taskId = JMetalRandom.getInstance().nextInt(0, 1000);
          initialTaskList.add(ParallelTask.create(taskId, solution));
        });

    return initialTaskList ;
  }

  @Override
  public void submitInitialTasks(List<ParallelTask> initialTaskList) {
    if (initialTaskList.size() >= numberOfCores) {
      initialTaskList.forEach(this::submitTask);
    } else {
      int idleWorkers = numberOfCores - initialTaskList.size();
      initialTaskList.forEach(this::submitTask);
      while (idleWorkers > 0) {
        submitTask(createNewTask());
        idleWorkers--;
      }
    }
  }

  @Override
  public void processComputedTask(ParallelTask task) {
    evaluations++;
    if (population.size() < populationSize) {
      population.add(task.getContents());
    } else {
      List<TM_MSASolution> offspringPopulation = new ArrayList<>(1);
      offspringPopulation.add(task.getContents());

      population = replacement.replace(population, offspringPopulation);
      Check.that(population.size() == populationSize, "The population size is incorrect");
    }
  }

  @Override
  public void submitTask(ParallelTask task) {
    pendingTaskQueue.add(task);
  }

  @Override
  public ParallelTask createNewTask() {
    if (population.size() > 2) {
      List<TM_MSASolution> parents = new ArrayList<>(2);
      parents.add(selection.execute(population));
      parents.add(selection.execute(population));

      List<TM_MSASolution> offspring = crossover.execute(parents);

      mutation.execute(offspring.get(0));

      return ParallelTask.create(createTaskIdentifier(), offspring.get(0));
    } else {
      return ParallelTask.create(createTaskIdentifier(), new PreComputedMSAsSolutionsCreation(
          (StandardTMMSAProblem) problem, 1).create().get(0));
    }
  }

  @Override
  public boolean stoppingConditionIsNotMet() {
    return !termination.isMet(attributes);
  }

  @Override
  public void run() {
    initTime = System.currentTimeMillis();
    super.run();
  }

  @Override
  public List<TM_MSASolution> getResult() {
    return population;
  }

  public Observable<Map<String, Object>> getObservable() {
    return observable;
  }
}
