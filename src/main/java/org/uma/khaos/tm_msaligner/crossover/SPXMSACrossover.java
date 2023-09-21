package org.uma.khaos.tm_msaligner.crossover;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;

public class SPXMSACrossover implements CrossoverOperator<TM_MSASolution> {
  private final JMetalRandom randomGenerator;
  private final double probability;

  public SPXMSACrossover(double probability) {
    Check.probabilityIsValid(probability);

    this.randomGenerator = JMetalRandom.getInstance();
    this.probability = probability;
  }

  /**
   * Checks conditions and return the result of performing single point crossover
   *
   * @param parents
   * @return
   */
  public List<TM_MSASolution> execute(List<TM_MSASolution> parents) {
    Check.notNull(parents);
    Check.that(parents.size() == 2, "The number of parents is not 2");
    Check.that(
        parents.get(0).variables().size() == parents.get(1).variables().size(),
        "The two parents have different length: "
            + parents.get(0).variables().size()
            + ", "
            + parents.get(1).variables().size());

    return doCrossover(parents);
  }

  /**
   * Performs a single point crossover of two parents. Uses the same cutting point for all sequences
   *
   * @return a list containing the generated offspring
   */
  private List<TM_MSASolution> doCrossover(List<TM_MSASolution> parents) {
    TM_MSASolution parent1 = parents.get(0);
    TM_MSASolution parent2 = parents.get(1);

    List<TM_MSASolution> children = new ArrayList<TM_MSASolution>();

    children.add(MSACrossover(parent1, parent2));
    children.add(MSACrossover(parent2, parent1));

    return children;
  }

  private TM_MSASolution MSACrossover(TM_MSASolution parentA, TM_MSASolution parentB) {
    TM_MSASolution child;
    if (this.randomGenerator.nextDouble() < this.probability) {
      int cut = selectRandomColumn(parentA);

      List<List<Integer>> gapsGroupFirstBloq = new ArrayList<List<Integer>>();
      List<Integer> carsCounterParentA = new ArrayList<Integer>();
      List<Integer> gapsGroup;
      int numgaps;

      for (int i = 0; i < parentA.variables().size(); i++) {
        gapsGroup = parentA.variables().get(i);

        List<Integer> gaps = new ArrayList<Integer>();
        numgaps = 0;
        for (int j = 1; j < gapsGroup.size(); j += 2) {
          if (cut >= gapsGroup.get(j)) {
            gaps.add(gapsGroup.get(j - 1));
            gaps.add(gapsGroup.get(j));
            numgaps += gapsGroup.get(j) - gapsGroup.get(j - 1) + 1;
          } else {
            if (cut >= gapsGroup.get(j - 1)) {
              gaps.add(gapsGroup.get(j - 1));
              gaps.add(cut);
              numgaps += cut - gapsGroup.get(j - 1) + 1;
            }
            break;
          }
        }
        gapsGroupFirstBloq.add(gaps);
        carsCounterParentA.add(cut - numgaps + 1);
      }

      int carsCountParentB;
      List<Integer> positionsToCutParentB = new ArrayList<Integer>();

      for (int i = 0; i < parentB.variables().size(); i++) {
        gapsGroup = parentB.variables().get(i);

        if (gapsGroup.size() > 0) {
          carsCountParentB = 0;
          for (int j = 0; j < gapsGroup.size(); j += 2) {
            if (j > 0) carsCountParentB += gapsGroup.get(j) - gapsGroup.get(j - 1) - 1;
            else carsCountParentB += gapsGroup.get(j);

            if (carsCountParentB >= carsCounterParentA.get(i)) {
              positionsToCutParentB.add(
                  gapsGroup.get(j) - (carsCountParentB - carsCounterParentA.get(i)));
              break;
            }
          }

          if (carsCountParentB < carsCounterParentA.get(i)) {
            if (gapsGroup.size() > 0) {
              carsCountParentB =
                  gapsGroup.get(gapsGroup.size() - 1)
                      + (carsCounterParentA.get(i) - carsCountParentB)
                      + 1;
              // if(carsCountParentB >= parent2.sizeAligment )
              // carsCountParentB=parent2.sizeAligment-1;
              positionsToCutParentB.add(carsCountParentB);
            }
          }
        } else { // SeqB has not Gaps
          positionsToCutParentB.add(carsCounterParentA.get(i));
        }
      }

      Integer MinPos = Collections.min(positionsToCutParentB);
      int pos;
      List<Integer> gaps;
      int lastGap, posA;
      for (int i = 0; i < parentB.variables().size(); i++) {
        posA = cut;
        pos = positionsToCutParentB.get(i);
        gaps = gapsGroupFirstBloq.get(i);
        if (pos > MinPos) {
          if (gaps.size() > 0) {
            lastGap = gaps.get(gaps.size() - 1);
            if (lastGap != posA) {
              gaps.add(posA + 1);
              gaps.add(posA + (pos - MinPos));
            } else {
              gaps.set(gaps.size() - 1, posA + (pos - MinPos));
            }
          } else {
            gaps.add(posA + 1);
            gaps.add(posA + (pos - MinPos));
          }

          posA += (pos - MinPos);
        }

        gapsGroup = parentB.variables().get(i);
        for (int j = 0; j < gapsGroup.size(); j += 2) {

          if (gapsGroup.get(j) >= pos) {
            gaps.add(posA + (gapsGroup.get(j) - pos) + 1);
            gaps.add(posA + (gapsGroup.get(j + 1) - pos) + 1);
          }
        }
      }

      child = new TM_MSASolution(parentA.getMSAProblem(), gapsGroupFirstBloq);

      child.mergeGapsGroups();

    } else {

      child = new TM_MSASolution(parentA);
    }
    return child;
  }

  /** Select a column randomly */
  public int selectRandomColumn(TM_MSASolution solution) {
    return randomGenerator.nextInt(1, solution.getAlignmentLength() - 1);
  }

  public int numberOfRequiredParents() {
    return 2;
  }

  public int numberOfGeneratedChildren() {
    return 2;
  }

  public double crossoverProbability() {
	 return probability;
}


}
