package org.tm_msaligner.problem.impl;

import java.io.IOException;
import java.util.List;
import org.tm_msaligner.problem.StandardTMMSAProblem;
import org.tm_msaligner.score.Score;
import org.tm_msaligner.score.impl.AlignedSegment;
import org.tm_msaligner.solution.TM_MSASolution;
import org.tm_msaligner.util.AA;

public class SingleObjTMMSAProblem extends StandardTMMSAProblem {

  private final Score score;

  public SingleObjTMMSAProblem(String msaProblemFileName, Score score,
      List<String> preComputedFiles) throws IOException {
    super(msaProblemFileName, preComputedFiles);

    setNumberOfObjectives(1);
    setName("Single Objective TM-MSA Problem");

    if (score.name() == "AlignedSegments") {
      AlignedSegment scoreAS = (AlignedSegment) score;
      if (scoreAS.isNormalized()) {
        scoreAS.setMaxSegmentAlignScore(MaxMinSegmentAlignScore[0]);
        scoreAS.setMinSegmentAlignScore(MaxMinSegmentAlignScore[1]);
      }
    }

    this.score = score;
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
