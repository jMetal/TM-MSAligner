package org.uma.khaos.tm_msaligner.algorithm.multiobjective;

import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;

public class TM_M2Align extends EvolutionaryAlgorithm<TM_MSASolution> {

    public TM_M2Align(SolutionsCreation<TM_MSASolution> initialPopulationCreation,
                      Evaluation<TM_MSASolution> evaluation,
                      Termination termination,
                      Selection<TM_MSASolution> selection,
                      Variation<TM_MSASolution> variation,
                      Replacement<TM_MSASolution> replacement) {
        super("TM-M2Align", initialPopulationCreation, evaluation, termination, selection, variation, replacement);

    }

}
