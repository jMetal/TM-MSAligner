package org.tm_msaligner.problem.impl;


import java.io.IOException;
import java.util.List;
import org.tm_msaligner.problem.StandardTMMSAProblem;
import org.tm_msaligner.score.Score;
import org.tm_msaligner.score.impl.AlignedSegment;
import org.tm_msaligner.solution.TM_MSASolution;
import org.tm_msaligner.util.AA;

public class MultiObjTMMSAProblem extends StandardTMMSAProblem {

  private final List<Score> scoreList;

  public MultiObjTMMSAProblem(String msaProblemFileName, List<Score> scoreList,
      List<String> preComputedFiles, String Name) throws IOException {
    super(msaProblemFileName, preComputedFiles);

    setNumberOfObjectives(scoreList.size());
    setName(Name); // "Multi Objective TM-MSA Problem"

    for (int i = 0; i < scoreList.size(); i++) {
      if (scoreList.get(i).name() == "AlignedSegments") {
        AlignedSegment scoreAS = (AlignedSegment) scoreList.get(i);
        if (scoreAS.isNormalized()) {
          scoreAS.setMaxSegmentAlignScore(MaxMinSegmentAlignScore[0]);
          scoreAS.setMinSegmentAlignScore(MaxMinSegmentAlignScore[1]);
        }
      }
    }
    this.scoreList = scoreList;
  }

  @Override
  public TM_MSASolution evaluate(TM_MSASolution solution) {
    solution.removeGapColumns();
    AA[][] decodedSequences = solution.decodeToMatrix();

    for (int i = 0; i < numberOfObjectives(); i++) {
      solution.objectives()[i] = scoreList.get(i).compute(solution, decodedSequences) *
          (scoreList.get(i).isAMinimizationScore() ? 1.0 : -1.0);
    }

    return solution;
  }
}
