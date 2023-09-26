package org.tm_msaligner.score.impl;


import org.tm_msaligner.score.Score;
import org.tm_msaligner.solution.TM_MSASolution;
import org.tm_msaligner.util.substitutionmatrix.impl.Blosum62;
import org.tm_msaligner.util.substitutionmatrix.impl.Phat;
import org.tm_msaligner.util.AA;

public class SumOfPairsWithTopologyPredict implements Score {

  public Phat phatMatrix;
  public Blosum62 blosum62Matrix;
  public double weightGapOpenTM;
  public double weightGapOpenNonTM;
  public double weightGapExtendTM;
  public double weightGapExtendNonTM;

  double wSOP = Double.MIN_VALUE;

  public SumOfPairsWithTopologyPredict(
      Phat phatMatrix,
      Blosum62 blosum62Matrix,
      double weightGapOpenTM,
      double weightGapExtendTM,
      double weightGapOpenNonTM,
      double weightGapExtendNonTM) {
    this.phatMatrix = phatMatrix;
    this.blosum62Matrix = blosum62Matrix;
    this.weightGapOpenTM = weightGapOpenTM;
    this.weightGapExtendTM = weightGapExtendTM;
    this.weightGapOpenNonTM = weightGapOpenNonTM;
    this.weightGapExtendNonTM = weightGapExtendNonTM;
  }

  public String getName() {
    return "SOPwTP";
  }

  public String getDescription() {
    return "Sum Of Pairs With Topology Prediction";
  }

  public <S extends TM_MSASolution> double compute(S solution, AA[][] decodedSequences) {
    int lengthSequences = solution.getAlignmentLength();
    int numberOfVariables = solution.variables().size();

    double tmSOP = 0;
    int i, j;
    AA aaA, aaB;
    boolean gapOpen = false;


    for (i = 0; i < numberOfVariables - 1; i++) {
      for (j = i+1; j < numberOfVariables; j++) {
        gapOpen=false;
        for (int l = 0; l < lengthSequences; l++) {
          aaA = decodedSequences[i][l];
          aaB = decodedSequences[j][l];
          if(aaA.isGap() || aaB.isGap()) {
              if(!gapOpen) {
                gapOpen=true;
                tmSOP -=  aaA.getType().isTMRegion()?weightGapOpenTM:weightGapOpenNonTM;
              }else{
                tmSOP -= aaA.getType().isTMRegion()?weightGapExtendTM:weightGapExtendNonTM;
              }
          }else {
            if (aaA.getType().isTMRegion() && aaB.getType().isTMRegion())
                  tmSOP += phatMatrix.getDistance(aaA.getLetter(), aaB.getLetter());
            else
                  tmSOP += blosum62Matrix.getDistance(aaA.getLetter(), aaB.getLetter());

            if(gapOpen) gapOpen = false;

          }
        }

      }

    }

    return tmSOP;
  }

  public boolean isAMinimizationScore() {    return false;  }

  @Override
  public String name() {
    return "Sum of pairs with topology predict" ;
  }

  @Override
  public String description() {
    return "Sum of pairs with topology predicr" ;
  }
}
