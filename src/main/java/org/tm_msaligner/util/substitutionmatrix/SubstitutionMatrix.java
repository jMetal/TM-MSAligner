package org.tm_msaligner.util.substitutionmatrix;

public abstract class SubstitutionMatrix {

  public static final int DEFAULT_GAP_PENALTY = -8;
  public int gapPenalty;
  public int[][] matrix;

  public SubstitutionMatrix(int gapPenalty) {
    this.gapPenalty = gapPenalty;
    loadMatrix();
  }

  public SubstitutionMatrix() {
    this(DEFAULT_GAP_PENALTY);
  }

  public abstract void loadMatrix();

  public int getGapPenalty() {
    return gapPenalty;
  }

  public int getDistance(char a1, char a2) {
    return matrix[get(a1)][get(a2)];
  }

  public abstract int get(char c);

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder("    ");
    for (int i = 0; i < matrix.length; i++) {
      if (i < 10) {
        result.append("   ").append(i);
      } else {
        result.append("  ").append(i);
      }
    }
    result.append("\n    ");
    for (int i = 0; i < matrix.length; i++) {
      result.append("----");
    }
    result.append("\n");

    for (int i = 0; i < matrix.length; i++) {
      if (i > 9) {
        result.append(i).append(" | ");
      } else {
        result.append(" ").append(i).append(" | ");
      }

      for (int j = 0; j < matrix.length; j++) {
        int value = matrix[i][j];
        if ((value < 0) || (value > 9)) {
          result.append(" ").append(matrix[i][j]).append(" ");
        } else {
          result.append("  ").append(matrix[i][j]).append(" ");
        }
      }
      result.append("\n");
    }

    return result.toString();
  }
}
