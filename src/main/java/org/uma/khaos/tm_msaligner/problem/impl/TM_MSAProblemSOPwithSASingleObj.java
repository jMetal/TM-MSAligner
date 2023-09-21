package org.uma.khaos.tm_msaligner.problem.impl;

import java.io.IOException;
import java.util.List;
import org.uma.khaos.tm_msaligner.problem.StandardTMMSAProblem;
import org.uma.khaos.tm_msaligner.score.impl.SumOfPairsWithAlignedSegment;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;
import org.uma.khaos.tm_msaligner.util.AA;

public class TM_MSAProblemSOPwithSASingleObj extends StandardTMMSAProblem {

  private final SumOfPairsWithAlignedSegment score;

  public TM_MSAProblemSOPwithSASingleObj(String msaProblemFileName,
      List<String> preComputedFiles,
      double weightGapOpenTM,
      double weightGapExtendTM,
      double weightGapOpenNonTM,
      double weightGapExtendNonTM) throws IOException {
    super(msaProblemFileName, preComputedFiles);

    setNumberOfObjectives(1);
    setName("TM-MSA Problem SumOfPairs With Segment Aligned Single Objective");

    score = new SumOfPairsWithAlignedSegment(MaxMinSegmentAlignScore[1],
        MaxMinSegmentAlignScore[0],
        weightGapOpenTM,
        weightGapExtendTM,
        weightGapOpenNonTM,
        weightGapExtendNonTM);
  }

  @Override
  public TM_MSASolution evaluate(TM_MSASolution solution) {
    solution.removeGapColumns();
    AA[][] decodedSequences = solution.decodeToMatrix();

    solution.objectives()[0] = score.compute(solution, decodedSequences) *
        (score.isAMinimizationScore() ? 1.0 : -1.0);

    return solution;
  }


}
