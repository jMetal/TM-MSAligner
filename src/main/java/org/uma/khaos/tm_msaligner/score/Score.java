package org.uma.khaos.tm_msaligner.score;

import org.uma.jmetal.util.naming.DescribedEntity;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;
import org.uma.khaos.tm_msaligner.util.AA;

public interface Score extends DescribedEntity {
  <S extends TM_MSASolution> double compute(S solution, AA[][]decodedSequences);
  boolean isAMinimizationScore();
}