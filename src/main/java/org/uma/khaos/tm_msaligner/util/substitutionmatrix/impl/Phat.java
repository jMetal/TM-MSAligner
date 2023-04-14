package org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl;

import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.SubstitutionMatrix;


public class Phat extends SubstitutionMatrix {

  public void loadMatrix() {

    matrix =
        new int[][] {
          // A   R   N   D   C   Q   E   G   H   I   L   K   M   F   P   S   T   W   Y   V   *
          // 0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16  17  18  19  20
          /* A */ {5, -6, -2, -5,  1, -3, -5,  1, -3,  0, -1, -7, -1, -1, -3,  2,  0, -4, -3,  1, gapPenalty},
          /* R */ {-6, 9, -3, -7, -8, -2, -6, -5, -4, -6, -6, -1, -6, -7, -7, -6, -6, -7, -6, -7, gapPenalty},
          /* N */ {-2, -3, 11, 2, -2, 2, 0, -1, 4, -3, -3, -2, -2, -1, -4, 1, -1, -5, 2, -3, gapPenalty},
          /* D */ {-5, -7, 2, 12, -7, 0, 6, -2, -1, -5, -5, -5, -5, -5, -5, -4, -5, -7, -4, -5, gapPenalty},
          /* C */ {1, -8, -2, -7, 7, -5, -7, -2, -7, -3, -2, -10, -2, 0, -8, 1, -1, -4, -1, -2, gapPenalty},
          /* Q */ {-3, -2, 2, 0, -5, 9, 1, -2, 2, -3, -3, -1, -1, -2, -3, -1, -3, 1, 0, -3, gapPenalty},
          /* E */ {-5, -6, 0, 6, -7, 1, 12, -3, -1, -5, -5, -4, -5, -5, -5, -3, -5, -7, -2, -5, gapPenalty},
          /* G */ {1, -5, -1, -2, -2, -2, -3, 9, -4, -2, -2, -5, -1, -2, -3, 1, -1, -5, -3, -2, gapPenalty},
          /* H */ {-3, -4, 4, -1, -7, 2, -1, -4, 11, -5, -4, -5, -4, -2, -6, -2, -4, -3, 3, -5, gapPenalty},
          /* I */ {0, -6, -3, -5, -3, -3, -5, -2, -5, 5, 2, -7, 3, 0, -4, -2, -1, -4, -3, 3, gapPenalty},
          /* L */ {-1, -6, -3, -5, -2, -3, -5, -2, -4, 2, 4, -7, 2, 1, -5, -2, -1, -3, -2, 1, gapPenalty},
          /* K */ {-7, -1, -2, -5, -10, -1, -4, -5, -5, -7, -7, 5, -6, -7, -4, -5, -6, -8, -4, -8, gapPenalty},
          /* M */ {-1, -6, -2, -5, -2, -1, -5, -1, -4, 3, 2, -6, 6, 0, -5, -2, 0, -4, -2, 1, gapPenalty},
          /* F */ {-1, -7, -1, -5, 0, -2, -5, -2, -2, 0, 1, -7, 0, 6, -5, -2, -2, 0, 4, -1, gapPenalty},
          /* P */ {-3, -7, -4, -5, -8, -3, -5, -3, -6, -4, -5, -4, -5, -5, 13, -3, -4, -6, -5, -4, gapPenalty},
          /* S */ {2, -6, 1, -4, 1, -1, -3, 1, -2, -2, -2, -5, -2, -2, -3, 6, 1, -5, -2, -2, gapPenalty},
          /* T */ {0, -6, -1, -5, -1, -3, -5, -1, -4, -1, -1, -6, 0, -2, -4, 1, 3, -7, -3, 0, gapPenalty},
          /* W */ {-4, -7, -5, -7, -4, 1, -7, -5, -3, -4, -3, -8, -4, 0, -6, -5, -7, 11, 1, -4, gapPenalty},
          /* Y */ {-3, -6, 2, -4, -1, 0, -2, -3, 3, -3, -2, -4, -2, 4, -5, -2, -3, 1, 11, -3, gapPenalty},
          /* V */ {1, -7, -3, -5, -2, -3, -5, -2, -5, 3, 1, -8, 1, -1, -4, -2, 0, -4, -3, 4, gapPenalty},
          /* - */ {gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty}
        };
  }

  public Phat(int gapPenalty) {
    super(gapPenalty);
  }

  public Phat() {
    super();
  }

  public int get(char c) {
    switch (c) {
      case 'A':
        return 0;
      case 'R':
        return 1;
      case 'N':
        return 2;
      case 'D':
        return 3;
      case 'C':
        return 4;
      case 'Q':
        return 5;
      case 'E':
        return 6;
      case 'G':
        return 7;
      case 'H':
        return 8;
      case 'I':
        return 9;
      case 'L':
        return 10;
      case 'K':
        return 11;
      case 'M':
        return 12;
      case 'F':
        return 13;
      case 'P':
        return 14;
      case 'S':
        return 15;
      case 'T':
        return 16;
      case 'W':
        return 17;
      case 'Y':
        return 18;
      case 'V':
        return 19;
      case '-':
        return 20;
      default:
        throw new JMetalException("Invalid char: " + c);
    }
  }
}
