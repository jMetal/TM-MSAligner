package org.uma.khaos.tm_msaligner.auto.parameter;

import java.util.List;

import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.khaos.tm_msaligner.mutation.InsertARandomGapMSAMutation;
import org.uma.khaos.tm_msaligner.mutation.MergeAdjunctedGapsGroupsMSAMutation;
import org.uma.khaos.tm_msaligner.mutation.ShiftClosedGapsMSAMutation;
import org.uma.khaos.tm_msaligner.mutation.SplitANonGapsGroupMSAMutation;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;

public class MutationMSAParameter extends CategoricalParameter {

  public MutationMSAParameter(List<String> mutationOperators) {
    super("mutation", mutationOperators);
  }

  public MutationOperator<TM_MSASolution> getParameter() {
    MutationOperator<TM_MSASolution> result;
    int sequenceLength = (int) getNonConfigurableParameter("sequenceLength");
    double mutationProbability = (double) findGlobalParameter(
        "mutationProbabilityFactor").value() / sequenceLength;

    result = switch (value()) {
      case "insertRandomGap" -> new InsertARandomGapMSAMutation(mutationProbability);
      case "mergeAdjuntedGapsGroups" -> new MergeAdjunctedGapsGroupsMSAMutation(mutationProbability) ;
      case "shiftClosedGaps" -> new ShiftClosedGapsMSAMutation(mutationProbability) ;
      case "splitANonGapsGroup" -> new SplitANonGapsGroupMSAMutation(mutationProbability);

      default -> throw new JMetalException("Mutation operator does not exist: " + name());
    };
    return result;
  }

  @Override
  public String name() {
    return "mutation";
  }
}
