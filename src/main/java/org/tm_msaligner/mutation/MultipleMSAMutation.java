package org.tm_msaligner.mutation;

import java.util.List;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.tm_msaligner.solution.TM_MSASolution;

public class MultipleMSAMutation implements MutationOperator<TM_MSASolution> {

  private double mutationProbability;
  private JMetalRandom randomGenerator;
  private List<MutationOperator<TM_MSASolution>> mutationOperatorList;


  public MultipleMSAMutation(double mutationProbability,
      List<MutationOperator<TM_MSASolution>> mutationOperatorList) {
    if ((mutationProbability < 0) || (mutationProbability > 1)) {
      throw new JMetalException("Mutation probability value invalid: " + mutationProbability);
    } else if (null == mutationOperatorList) {
      throw new JMetalException("The operator list is null");
    }

    this.mutationProbability = mutationProbability;
    this.mutationOperatorList = mutationOperatorList;
    randomGenerator = JMetalRandom.getInstance();
  }

  @Override
  public TM_MSASolution execute(TM_MSASolution solution) {
    if (null == solution) {
      throw new JMetalException("Null parameter");
    }

    if (randomGenerator.nextDouble() < mutationProbability) {
      for (MutationOperator<TM_MSASolution> mutation : mutationOperatorList) {
        solution = mutation.execute(solution);
      }
    }

    return solution;
  }


  @Override
  public double mutationProbability() {
    return mutationProbability;
  }
}

