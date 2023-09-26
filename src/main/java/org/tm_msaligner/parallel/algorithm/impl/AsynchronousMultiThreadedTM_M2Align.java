package org.tm_msaligner.parallel.algorithm.impl;

import org.tm_msaligner.solution.TM_MSASolution;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.ranking.impl.MergeNonDominatedSortRanking;

public class AsynchronousMultiThreadedTM_M2Align
    extends AsynchronousMultiThreadedGeneticAlgorithm {

  public AsynchronousMultiThreadedTM_M2Align(
      int numberOfCores,
      Problem<TM_MSASolution> problem,
      int populationSize,
      CrossoverOperator<TM_MSASolution> crossover,
      MutationOperator<TM_MSASolution> mutation,
      Termination termination) {
    super(numberOfCores,problem, populationSize, crossover,mutation, new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>()),
            new RankingAndDensityEstimatorReplacement<>(
                    new MergeNonDominatedSortRanking<>(),
                    new CrowdingDistanceDensityEstimator<>(),
                    Replacement.RemovalPolicy.ONE_SHOT),termination);
  }
}
