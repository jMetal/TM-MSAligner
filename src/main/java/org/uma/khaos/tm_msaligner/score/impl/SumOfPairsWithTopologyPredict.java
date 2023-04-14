package org.uma.khaos.tm_msaligner.score.impl;


import org.uma.khaos.tm_msaligner.score.Score;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;
import org.uma.khaos.tm_msaligner.util.AA;
import org.uma.khaos.tm_msaligner.util.BaseType;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Blosum62;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Phat;

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
    int numGapsExtended =0;
    BaseType lastTypeStarGapOpen;

    for (i = 0; i < numberOfVariables - 1; i++) {
      for (j = i+1; j < numberOfVariables; j++) {
        gapOpen=false;
        numGapsExtended=0;
        lastTypeStarGapOpen=decodedSequences[i][0].getType();
        for (int l = 0; l < lengthSequences; l++) {
          aaA = decodedSequences[i][l];
          aaB = decodedSequences[j][l];
          if(aaA.isGap() || aaA.isGap()) {
              if(!gapOpen) {
                gapOpen=true;
                tmSOP-=  aaA.getType().isTMRegion()?weightGapOpenTM:weightGapOpenNonTM;
                numGapsExtended=0;
                lastTypeStarGapOpen=aaA.getType();
              }else{
                numGapsExtended++;
              }
          }else {
            if (aaA.getType().isTMRegion() && aaB.getType().isTMRegion())
              tmSOP += phatMatrix.getDistance(aaA.getLetter(), aaB.getLetter());
            else tmSOP += blosum62Matrix.getDistance(aaA.getLetter(), aaB.getLetter());
            if(gapOpen) {
              gapOpen = false;
              tmSOP -= numGapsExtended * (lastTypeStarGapOpen.isTMRegion()?weightGapExtendTM:weightGapExtendNonTM);
              numGapsExtended = 0;
            }
          }
        }

        if(gapOpen) {
          tmSOP -= numGapsExtended * (lastTypeStarGapOpen.isTMRegion()?weightGapExtendTM:weightGapExtendNonTM);
        }



      }

    }

    return tmSOP;
  }

  public boolean isAMinimizationScore() {    return false;  }
}
