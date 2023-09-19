package org.uma.khaos.tm_msaligner.algorithm.singleobjective;

import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.MultiThreadedEvaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.MuPlusLambdaReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.khaos.tm_msaligner.crossover.SPXMSACrossover;
import org.uma.khaos.tm_msaligner.mutation.ShiftClosedGapsMSAMutation;
import org.uma.khaos.tm_msaligner.problem.StandardTMMSAProblem;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;
import org.uma.khaos.tm_msaligner.solutionscreation.PreComputedMSAsSolutionsCreation;

public class TM_AlignGABuilder {

    private Evaluation<TM_MSASolution> evaluation;
    private PreComputedMSAsSolutionsCreation createInitialPopulation;
    private Termination termination;
    private Selection<TM_MSASolution> selection;
    private Variation<TM_MSASolution> variation;
    private Replacement<TM_MSASolution> replacement;
    private CrossoverOperator<TM_MSASolution> crossover;
    private MutationOperator<TM_MSASolution> mutation;


    public TM_AlignGABuilder(StandardTMMSAProblem problem, int maxEvaluations,
                             int populationSize, int offspringPopulationSize,
                             double probabilityCrossover, double probabilityMutation,
                             int numCores) {

        crossover = new SPXMSACrossover(probabilityCrossover);
        mutation = new ShiftClosedGapsMSAMutation(probabilityMutation);
        variation = new CrossoverAndMutationVariation<>(
                offspringPopulationSize, crossover, mutation);

        selection = new NaryTournamentSelection<>(2, variation.getMatingPoolSize(),
                new ObjectiveComparator<>(0));
        replacement = new MuPlusLambdaReplacement<>(new ObjectiveComparator<>(0));

        createInitialPopulation = new PreComputedMSAsSolutionsCreation(problem, populationSize);

        if(numCores>1){
            evaluation = new MultiThreadedEvaluation<>(numCores, problem) ;
        }else{
            evaluation= new SequentialEvaluation<>(problem);
        }
        termination = new TerminationByEvaluations(maxEvaluations);


    }

    public TM_AlignGABuilder setEvaluation(Evaluation<TM_MSASolution> evaluation) {
        this.evaluation = evaluation;

        return this;
    }

    public TM_AlignGA build() {
        return new TM_AlignGA(createInitialPopulation,
                evaluation, termination,
                selection, variation, replacement);
    }


}
