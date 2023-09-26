package org.tm_msaligner.mutation;

import java.util.List;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.tm_msaligner.solution.TM_MSASolution;

public class InsertARandomGapMSAMutation implements MutationOperator<TM_MSASolution> {
  private double mutationProbability;
  private JMetalRandom randomGenerator;

  public InsertARandomGapMSAMutation(double mutationProbability) {
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
       
       int sizeOfTheAlignment=solution.getAlignmentLength();
       List<Integer> newGapGroupWithARandomGap;
       for(int i=0; i<solution.variables().size();i++) {
           newGapGroupWithARandomGap = insertARandomGap(solution.variables().get(i),sizeOfTheAlignment);
           solution.variables().set(i,newGapGroupWithARandomGap);
      }
      
       solution.mergeGapsGroups();
    }
  }



  public List<Integer>  insertARandomGap(List<Integer> gapsGroup, int sizeOfTheAlignment) {
    Integer posRandomGap;
    posRandomGap = randomGenerator.nextInt(0, sizeOfTheAlignment-1);
    int i=0;
    boolean added=false;
    for (i = 0; i < gapsGroup.size() - 1; i += 2) {
        if(gapsGroup.get(i)>posRandomGap){
            gapsGroup.add(i,posRandomGap); gapsGroup.add(i,posRandomGap);
            i+=2;
            added=true;
            break;
        }
        if (posRandomGap>=gapsGroup.get(i)  && posRandomGap<=gapsGroup.get(i + 1)) {
            i++;
            added=true;
            break;
        }         
    }
    if(added){
        while(i<gapsGroup.size()){
            gapsGroup.set(i,gapsGroup.get(i)+1);
            i++;
        }
    }else{
         gapsGroup.add(posRandomGap); gapsGroup.add(posRandomGap);
    }
    return gapsGroup;
  }

  @Override
  public double mutationProbability() {
    return mutationProbability;
  }
}

