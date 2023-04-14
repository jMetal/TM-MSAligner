package org.uma.khaos.tm_msaligner.score.impl;


import org.uma.khaos.tm_msaligner.score.Score;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;
import org.uma.khaos.tm_msaligner.util.AA;
import org.uma.khaos.tm_msaligner.util.BaseType;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Blosum62;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Phat;

public class SumOfPairsWithAlignedSegment implements Score {

  public Phat PhatMatrix;
  public Blosum62 Blosum62Matrix;
  public double weightGapOpenTM;
  public double weightGapOpenNonTM;
  public double weightGapExtendTM;
  public double weightGapExtendNonTM;

  public int getMatch(BaseType A, BaseType B) {

    if(A.isTMRegion())
      return B.isTMRegion()?2:-1;
    else
      return B.isNonTMRegion()?1:-1;

  }

  public SumOfPairsWithAlignedSegment(
      Phat phatMatrix,
      Blosum62 blosum62Matrix,
      double weightGapOpenTM,
      double weightGapExtendTM,
      double weightGapOpenNonTM,
      double weightGapExtendNonTM) {
    this.PhatMatrix = phatMatrix;
    this.Blosum62Matrix = blosum62Matrix;
    this.weightGapOpenTM = weightGapOpenTM;
    this.weightGapExtendTM = weightGapExtendTM;
    this.weightGapOpenNonTM = weightGapOpenNonTM;
    this.weightGapExtendNonTM = weightGapExtendNonTM;
  }

  public String getName() {
    return "SOPwTP&ASegment";
  }

  public String getDescription() {
    return "Sum Of Pairs With Topology Prediction and AlignedSegment";
  }

  public <S extends TM_MSASolution> double[] computebothScore(S solution, AA[][] decodedSequences) {
    int lengthSequences = solution.getAlignmentLength();
    int numberOfVariables = solution.variables().size();
    double tmSOP = 0,
        numGapsOpenTM = 0,
        numGapsOpenNonTM = 0,
        numGapsExtendTM = 0,
        numGapsExtendNonTM = 0;
    double sumAlignedSegments = 0;

    int i, j;
    AA aaA, aaB;
    BaseType aaAType;

    boolean[] isGapOpened = new boolean[numberOfVariables];
    //BaseType[] lastAAType = new BaseType[numberOfVariables];
    for (i = 0; i < numberOfVariables; i++) {
      isGapOpened[i] = false;
      //lastAAType[i] = new BaseType(-1); // unknown;
    }

    for (int l = 0; l < lengthSequences; l++) {
      for (i = 0; i < numberOfVariables - 1; i++) {
        aaA = decodedSequences[i][l];
        aaAType = aaA.getType();

        if (aaA.isGap()) {
          //if (aaAType.equals(lastAAType[i])) {
            if (!isGapOpened[i]) {
              isGapOpened[i] = true;
              if (aaAType.isTMRegion()) numGapsOpenTM++;
              else numGapsOpenNonTM++;
            } else {
              if (aaAType.isTMRegion()) numGapsExtendTM++;
              else numGapsExtendNonTM++;
            }
          /*} else {
            if (aaAType.isTMRegion()) numGapsOpenTM++;
            else numGapsOpenNonTM++;
            isGapOpened[i] = true;
          }*/

        } else {
          isGapOpened[i] = false;
        }

        for (j = i + 1; j < numberOfVariables; j++) {
          aaB = decodedSequences[j][l];
          if (aaAType.isTMRegion() && aaB.getType().isTMRegion())
            tmSOP += PhatMatrix.getDistance(aaA.getLetter(), aaB.getLetter());
          else tmSOP += Blosum62Matrix.getDistance(aaA.getLetter(), aaB.getLetter());

          sumAlignedSegments += getMatch(aaAType, aaB.getType());
        }

      }

      // El último caracter de la columna l
      aaA = decodedSequences[i][l];
      aaAType = aaA.getType();
      if (aaA.isGap()) {
        //if (aaAType.equals(lastAAType[i])) {
          if (!isGapOpened[i]) {
            isGapOpened[i] = true;
            if (aaAType.isTMRegion()) numGapsOpenTM++;
            else numGapsOpenNonTM++;
          } else {
            if (aaAType.isTMRegion()) numGapsExtendTM++;
            else numGapsExtendNonTM++;
          }
        /*} else {
          if (aaAType.isTMRegion()) numGapsOpenTM++;
          else numGapsOpenNonTM++;
          isGapOpened[i] = true;
        }*/

      } else {
        isGapOpened[i] = false;
      }
    }

    tmSOP =
        tmSOP
            + ((weightGapOpenTM * numGapsOpenTM)
                + (weightGapExtendTM * numGapsExtendTM)
                + (weightGapOpenNonTM * numGapsOpenNonTM)
                + (weightGapExtendNonTM * numGapsExtendNonTM));

    return new double[] {tmSOP, sumAlignedSegments};
  }

  public boolean isAMinimizationScore() {    return false;    }

  public <S extends TM_MSASolution> double compute(S solution, AA[][] decodedSequences) {
    return 0;
  }
}
