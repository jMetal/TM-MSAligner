package org.tm_msaligner.mutation;

import java.util.List;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.tm_msaligner.solution.TM_MSASolution;

public class MergeAdjunctedGapsGroupsMSAMutation implements MutationOperator<TM_MSASolution> {
  private double mutationProbability;
  private JMetalRandom randomGenerator;


  public MergeAdjunctedGapsGroupsMSAMutation(double mutationProbability) {
    if ((mutationProbability < 0) || (mutationProbability > 1)) {
      throw new JMetalException("Mutation probability value invalid: " + mutationProbability);
    }
    this.mutationProbability = mutationProbability;

    randomGenerator = JMetalRandom.getInstance();
  }

  @Override
  public TM_MSASolution execute(TM_MSASolution solution) {
    if (null == solution) {
      throw new JMetalException("Null parameter");
    }

    doMutation(solution);
    return solution;
  }

  /**
   * Performs the operation
   */
  public void doMutation(TM_MSASolution solution) {
    if (randomGenerator.nextDouble() < mutationProbability) {
       
       int selectedSequence=getSelectedSequenceWithMoreThanOneGapsGroups(solution);
       //System.out.println(selectedSequence);
       List<Integer> gapsGroup = solution.variables().get(selectedSequence);
       //System.out.println(gapsGroup.toString());
       int selectedGapsGroup= randomGenerator.nextInt(0, gapsGroup.size()/2-2);
       selectedGapsGroup*=2;
       //System.out.println(selectedGapsGroup);
       int numberOfGaps= gapsGroup.get(selectedGapsGroup+1)-gapsGroup.get(selectedGapsGroup)+1;
       gapsGroup.set(selectedGapsGroup+2, gapsGroup.get(selectedGapsGroup+2)-numberOfGaps);
       
       gapsGroup.remove(selectedGapsGroup); gapsGroup.remove(selectedGapsGroup);
       
       //System.out.println(gapsGroup.toString());
       solution.variables().set(selectedSequence,gapsGroup);
       
       solution.mergeGapsGroups();
    }
  }

 public int getSelectedSequenceWithMoreThanOneGapsGroups(TM_MSASolution solution) {
    int selectedSequence = -1;
    do {
      selectedSequence = randomGenerator.nextInt(0, solution.variables().size() - 1);
    }
    while (!hasMoreThanOneGapsGroups(solution.variables().get(selectedSequence)));

    return selectedSequence;
  }

  public boolean hasMoreThanOneGapsGroups(List<Integer> gapsGroup) {
     return gapsGroup.size()>2;
  }
  
  @Override
  public double mutationProbability() {
    return mutationProbability;
  }
}

