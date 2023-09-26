package org.tm_msaligner.util.substitutionmatrix.impl;


import org.tm_msaligner.util.substitutionmatrix.SubstitutionMatrix;

/**
 * Class implementing the PAM 250 substitution matrix
 *
 * <p>Matrix based on "pam" Version 1.0.7 [13-Aug-03] PAM 250 substitution matrix, scale = ln(2)/3 =
 * 0.231049
 *
 * <p>Expected score = -0.844, Entropy = 0.354 bits
 *
 * <p>Lowest score = -8, Highest score = 17
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class PAM250 extends SubstitutionMatrix {

  public void loadMatrix() {

    matrix =
        new int[][] {
          // A   R   N   D   C   Q   E   G   H   I   L   K   M   F   P   S   T   W   Y   V   B   Z
          // X  *
          // 0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16  17  18  19  20  21
          // 22 23
          /* A */ {
            2, -2, 0, 0, -2, 0, 0, 1, -1, -1, -2, -1, -1, -3, 1, 1, 1, -6, -3, 0, 0, 0, 0, gapPenalty
          },
          /* R */ {
            -2, 6, 0, -1, -4, 1, -1, -3, 2, -2, -3, 3, 0, -4, 0, 0, -1, 2, -4, -2, -1, 0, -1, gapPenalty
          },
          /* N */ {0, 0, 2, 2, -4, 1, 1, 0, 2, -2, -3, 1, -2, -3, 0, 1, 0, -4, -2, -2, 2, 1, 0, gapPenalty},
          /* D */ {
            0, -1, 2, 4, -5, 2, 3, 1, 1, -2, -4, 0, -3, -6, -1, 0, 0, -7, -4, -2, 3, 3, -1, gapPenalty
          },
          /* C */ {
            -2, -4, -4, -5, 12, -5, -5, -3, -3, -2, -6, -5, -5, -4, -3, 0, -2, -8, 0, -2, -4, -5,
            -3, gapPenalty
          },
          /* Q */ {
            0, 1, 1, 2, -5, 4, 2, -1, 3, -2, -2, 1, -1, -5, 0, -1, -1, -5, -4, -2, 1, 3, -1, gapPenalty
          },
          /* E */ {
            0, -1, 1, 3, -5, 2, 4, 0, 1, -2, -3, 0, -2, -5, -1, 0, 0, -7, -4, -2, 3, 3, -1, gapPenalty
          },
          /* G */ {
            1, -3, 0, 1, -3, -1, 0, 5, -2, -3, -4, -2, -3, -5, 0, 1, 0, -7, -5, -1, 0, 0, -1, gapPenalty
          },
          /* H */ {
            -1, 2, 2, 1, -3, 3, 1, -2, 6, -2, -2, 0, -2, -2, 0, -1, -1, -3, 0, -2, 1, 2, -1, gapPenalty
          },
          /* I */ {
            -1, -2, -2, -2, -2, -2, -2, -3, -2, 5, 2, -2, 2, 1, -2, -1, 0, -5, -1, 4, -2, -2, -1, gapPenalty
          },
          /* L */ {
            -2, -3, -3, -4, -6, -2, -3, -4, -2, 2, 6, -3, 4, 2, -3, -3, -2, -2, -1, 2, -3, -3, -1, gapPenalty
          },
          /* K */ {
            -1, 3, 1, 0, -5, 1, 0, -2, 0, -2, -3, 5, 0, -5, -1, 0, 0, -3, -4, -2, 1, 0, -1, gapPenalty
          },
          /* M */ {
            -1, 0, -2, -3, -5, -1, -2, -3, -2, 2, 4, 0, 6, 0, -2, -2, -1, -4, -2, 2, -2, -2, -1, gapPenalty
          },
          /* F */ {
            -3, -4, -3, -6, -4, -5, -5, -5, -2, 1, 2, -5, 0, 9, -5, -3, -3, 0, 7, -1, -4, -5, -1, gapPenalty
          },
          /* P */ {
            1, 0, 0, -1, -3, 0, -1, 0, 0, -2, -3, -1, -2, -5, 6, 1, 0, -6, -5, -1, -1, 0, -1, gapPenalty
          },
          /* S */ {
            1, 0, 1, 0, 0, -1, 0, 1, -1, -1, -3, 0, -2, -3, 1, 2, 1, -2, -3, -1, 0, 0, -1, gapPenalty
          },
          /* T */ {
            1, -1, 0, 0, -2, -1, 0, 0, -1, 0, -2, 0, -1, -3, 0, 1, 3, -5, -3, 0, 0, -1, -1, gapPenalty
          },
          /* W */ {
            -6, 2, -4, -7, -8, -5, -7, -7, -3, -5, -2, -3, -4, 0, -6, -2, -5, 17, 0, -6, -5, -6, -1,
				gapPenalty
          },
          /* Y */ {
            -3, -4, -2, -4, 0, -4, -4, -5, 0, -1, -1, -4, -2, 7, -5, -3, -3, 0, 10, -2, -3, -4, -1,
				gapPenalty
          },
          /* V */ {
            0, -2, -2, -2, -2, -2, -2, -1, -2, 4, 2, -2, 2, -1, -1, -1, 0, -6, -2, 4, -2, -2, -1, gapPenalty
          },
          /* B */ {
            0, -1, 2, 3, -4, 1, 3, 0, 1, -2, -3, 1, -2, -4, -1, 0, 0, -5, -3, -2, 3, 2, -1, gapPenalty
          },
          /* Z */ {
            0, 0, 1, 3, -5, 3, 3, 0, 2, -2, -3, 0, -2, -5, 0, 0, -1, -6, -4, -2, 2, 3, -1, gapPenalty
          },
          /* X */ {
            0, -1, 0, -1, -3, -1, -1, -1, -1, -1, -1, -1, -1, -2, -1, 0, 0, -4, -2, -1, -1, -1, -1,
				gapPenalty
          },
          /* - */ {gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, 1}
        };
  }

  public PAM250(int gapPenalty) {
    super(gapPenalty);
  }

  public PAM250() {
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
      case 'B':
        return 20;
      case 'Z':
        return 21;
      case 'X':
        return 22;
      case '-':
        return 23;
      default:
        throw new RuntimeException("Invalid char: " + c);
    }
  }
}
