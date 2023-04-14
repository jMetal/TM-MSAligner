package org.uma.khaos.tm_msaligner.score.impl;


import org.uma.khaos.tm_msaligner.score.Score;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;
import org.uma.khaos.tm_msaligner.util.AA;

public class AlignedSegment implements Score {

  public String getName() {
    return "Segments";
  }

  public String getDescription() {
    return "Aligned Topology Prediction Segments";
  }

  public int getMatch(AA A, AA B) {
    if(!A.isGap() && !B.isGap()){
      if(A.getType().isTMRegion()){
        if(B.getType().isTMRegion())
          return A.getLetter()==B.getLetter()?4:2;
        else
          return 0;
      }else{
        if(B.getType().isNonTMRegion())
          return A.getLetter()==B.getLetter()?2:1;
        else
          return 0;

      }
    }else
      return 0;

  }

  public <S extends TM_MSASolution> double compute(S solution, AA[][] decodedSequences) {

    int lengthSequences = solution.getAlignmentLength();
    int numberOfVariables = solution.variables().size();

    double sumAlignedSegments = 0;
    int i, j;
    for (int l = 0; l < lengthSequences; l++) {
      for (i = 0; i < numberOfVariables - 1; i++) {
        for (j = i + 1; j < numberOfVariables; j++) {
          sumAlignedSegments +=
              getMatch(decodedSequences[i][l], decodedSequences[j][l]);
        }
      }
    }

    return sumAlignedSegments;
  }

  public boolean isAMinimizationScore() {    return false;  }
}
