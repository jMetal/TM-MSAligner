package org.uma.khaos.tm_msaligner.algorithm.singleobjective;

import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;

public class TM_AlignGA extends EvolutionaryAlgorithm<TM_MSASolution> {

    public TM_AlignGA(SolutionsCreation<TM_MSASolution> initialPopulationCreation,
                      Evaluation<TM_MSASolution> evaluation,
                      Termination termination,
                      Selection<TM_MSASolution> selection,
                      Variation<TM_MSASolution> variation,
                      Replacement<TM_MSASolution> replacement) {
        super("TM-AlignGA", initialPopulationCreation, evaluation, termination, selection, variation, replacement);

    }

}
