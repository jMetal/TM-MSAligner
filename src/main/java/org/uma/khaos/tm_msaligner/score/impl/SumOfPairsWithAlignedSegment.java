package org.uma.khaos.tm_msaligner.score.impl;


import org.uma.khaos.tm_msaligner.score.Score;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;
import org.uma.khaos.tm_msaligner.util.AA;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Blosum62;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Phat;


public class SumOfPairsWithAlignedSegment implements Score {

  SumOfPairsWithTopologyPredict SOPScore;
  AlignedSegment SAScore;

  public SumOfPairsWithAlignedSegment(
          long MinSegmentAlignScore,
          long MaxSegmentAlignScore,
          double weightGapOpenTM,
          double weightGapExtendTM,
          double weightGapOpenNonTM,
          double weightGapExtendNonTM) {

    SOPScore = new SumOfPairsWithTopologyPredict(
            new Phat(8),
            new Blosum62(),
            weightGapOpenTM,
            weightGapExtendTM,
            weightGapOpenNonTM,
            weightGapExtendNonTM);

    SAScore = new AlignedSegment(true);
    SAScore.setMaxSegmentAlignScore(MaxSegmentAlignScore);
    SAScore.setMinSegmentAlignScore(MinSegmentAlignScore);

  }

  public String getName() {
    return "SOPwTP&ASegment";
  }

  public String getDescription() {
    return "Sum Of Pairs With Topology Prediction and AlignedSegment";
  }


  public boolean isAMinimizationScore() {    return false;    }

  public <S extends TM_MSASolution> double compute(S solution, AA[][] decodedSequences) {

    double SOP = SOPScore.compute(solution, decodedSequences);
    double normSA = SAScore.compute(solution, decodedSequences);
    return (SOP * normSA) + SOP;

  }

  @Override
  public String name() {
    return "Sum of pairs with alignment segment" ;
  }

  @Override
  public String description() {
    return "Sum of pairs with alignment segment" ;
  }
}
