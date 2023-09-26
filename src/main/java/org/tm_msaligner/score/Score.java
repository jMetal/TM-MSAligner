package org.tm_msaligner.score;

import org.tm_msaligner.solution.TM_MSASolution;
import org.uma.jmetal.util.naming.DescribedEntity;
import org.tm_msaligner.util.AA;

public interface Score extends DescribedEntity {
  <S extends TM_MSASolution> double compute(S solution, AA[][]decodedSequences);
  boolean isAMinimizationScore();
  String getName();
}