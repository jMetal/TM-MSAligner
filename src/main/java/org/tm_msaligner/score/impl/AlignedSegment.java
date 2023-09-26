package org.tm_msaligner.score.impl;

import org.tm_msaligner.score.Score;
import org.tm_msaligner.solution.TM_MSASolution;
import org.tm_msaligner.util.AA;

public class AlignedSegment implements Score {

  boolean Normalized;
  long MaxSegmentAlignScore;
  long MinSegmentAlignScore;

  public AlignedSegment() {
    this.Normalized = false;
  }

  public AlignedSegment(boolean Normalized) {
    this.Normalized = Normalized;
  }

  public void setMaxSegmentAlignScore(long MaxSegmentAlignScore) {
    this.MaxSegmentAlignScore = MaxSegmentAlignScore;
  }

  public void setMinSegmentAlignScore(long MinSegmentAlignScore) {
    this.MinSegmentAlignScore = MinSegmentAlignScore;
  }

  public boolean isNormalized() {
    return this.Normalized;
  }

  public String getName() {
    return "AlignedSegments";
  }

  public String getDescription() {
    return "Aligned Topology Prediction Segments";
  }

  public int getMatch(AA A, AA B) {
    if (A.getType().isTMRegion()) {
      if (B.getType().isTMRegion()) {
        return A.getLetter() == B.getLetter() ? 4 : 2;
      } else {
        return 0;
      }
    } else {
      if (B.getType().isNonTMRegion()) {
        return A.getLetter() == B.getLetter() ? 2 : 1;
      } else {
        return 0;
      }

    }
  }

  public <S extends TM_MSASolution> double compute(S solution, AA[][] decodedSequences) {

    int lengthSequences = solution.getAlignmentLength();
    int numberOfVariables = solution.variables().size();

    double sumAlignedSegments = 0;
    int i, j;
    for (int l = 0; l < lengthSequences; l++) {
      for (i = 0; i < numberOfVariables - 1; i++) {
        if (!decodedSequences[i][l].isGap()) {
          for (j = i + 1; j < numberOfVariables; j++) {
            if (!decodedSequences[j][l].isGap()) {
              sumAlignedSegments +=
                  getMatch(decodedSequences[i][l], decodedSequences[j][l]);
            }
          }
        }
      }
    }

    if (isNormalized()) {
      return (sumAlignedSegments - MinSegmentAlignScore) / (MaxSegmentAlignScore
          - MinSegmentAlignScore);
    } else {
      return sumAlignedSegments;
    }

  }

  public boolean isAMinimizationScore() {
    return false;
  }

  @Override
  public String name() {
    return "Aligned segment score" ;
  }

  @Override
  public String description() {
    return "Aligned segment score" ;
  }
}
