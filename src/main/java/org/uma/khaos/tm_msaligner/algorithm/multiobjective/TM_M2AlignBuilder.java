package org.uma.khaos.tm_msaligner.algorithm.multiobjective;

import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.MultiThreadedEvaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.khaos.tm_msaligner.crossover.SPXMSACrossover;
import org.uma.khaos.tm_msaligner.mutation.ShiftClosedGapsMSAMutation;
import org.uma.khaos.tm_msaligner.problem.StandardTMMSAProblem;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;
import org.uma.khaos.tm_msaligner.solutionscreation.PreComputedMSAsSolutionsCreation;

import java.util.Arrays;
import java.util.Comparator;

public class TM_M2AlignBuilder{

    Ranking<TM_MSASolution> ranking;

    private Evaluation<TM_MSASolution> evaluation;
    private PreComputedMSAsSolutionsCreation createInitialPopulation;
    private Termination termination;
    private Selection<TM_MSASolution> selection;
    private Variation<TM_MSASolution> variation;
    private Replacement<TM_MSASolution> replacement;
    private DensityEstimator<TM_MSASolution> densityEstimator;
    private CrossoverOperator<TM_MSASolution> crossover;
    private MutationOperator<TM_MSASolution> mutation;


    public TM_M2AlignBuilder(StandardTMMSAProblem problem, int maxEvaluations,
                             int populationSize, int offspringPopulationSize,
                            double probabilityCrossover, double probabilityMutation,
                            int numCores) {


        crossover = new SPXMSACrossover(probabilityCrossover);
        mutation = new ShiftClosedGapsMSAMutation(probabilityMutation);
        variation = new CrossoverAndMutationVariation<>(
                offspringPopulationSize, crossover, mutation);

        densityEstimator = new CrowdingDistanceDensityEstimator<>();
        ranking = new FastNonDominatedSortRanking<>();
        replacement = new RankingAndDensityEstimatorReplacement<>(
                        ranking, densityEstimator, Replacement.RemovalPolicy.ONE_SHOT);

        int tournamentSize = 2 ;
        selection= new NaryTournamentSelection<>(
                tournamentSize, variation.getMatingPoolSize(),
                new MultiComparator<>(
                        Arrays.asList(
                                Comparator.comparing(ranking::getRank),
                                Comparator.comparing(densityEstimator::value).reversed())));


        createInitialPopulation = new PreComputedMSAsSolutionsCreation(problem, populationSize);

        if(numCores>1){
            evaluation = new MultiThreadedEvaluation<>(numCores, problem) ;
        }else{
            evaluation= new SequentialEvaluation<>(problem);
        }


        termination = new TerminationByEvaluations(maxEvaluations);

    }

    public TM_M2AlignBuilder setEvaluation(Evaluation<TM_MSASolution> evaluation) {
        this.evaluation = evaluation;

        return this;
    }

    public TM_M2Align build() {
        return new TM_M2Align(createInitialPopulation,
                            evaluation, termination,
                            selection, variation, replacement);
    }

}
