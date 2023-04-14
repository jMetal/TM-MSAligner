package org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl;

import org.uma.khaos.tm_msaligner.util.substitutionmatrix.SubstitutionMatrix;

/**
 * Blosum62 substitution matrix
 *
 *  BLOSUM Clustered Scoring Matrix in 1/2 Bit Units
 #  Blocks Database = /data/blocks_5.0/blocks.dat
 #  Cluster Percentage: >= 62
 #  Entropy =   0.6979, Expected =  -0.5209
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

public class Blosum62 extends SubstitutionMatrix {

	public void loadMatrix() {
	  matrix = new int[][] {
	        //A   R   N   D   C   Q   E   G   H   I   L   K   M   F   P   S   T   W   Y   V   B   Z   X  *
	        //0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16  17  18  19  20  21  22 23
	/* A */ { 4, -1, -2, -2,  0, -1, -1,  0, -2, -1, -1, -1, -1, -2, -1,  1,  0, -3, -2,  0, -2, -1,  0, gapPenalty},
	/* R */ {-1,  5,  0, -2, -3,  1,  0, -2,  0, -3, -2,  2, -1, -3, -2, -1, -1, -3, -2, -3, -1,  0 ,-1, gapPenalty},
	/* N */ {-2,  0,  6,  1, -3,  0,  0,  0,  1, -3, -3,  0, -2, -3, -2,  1,  0, -4, -2, -3,  3,  0, -1, gapPenalty},
	/* D */ {-2, -2,  1,  6, -3,  0,  2, -1, -1, -3, -4, -1, -3, -3, -1,  0, -1, -4, -3, -3,  4,  1, -1, gapPenalty},
	/* C */ { 0, -3, -3, -3,  9, -3, -4, -3, -3, -1, -1, -3, -1, -2, -3, -1, -1, -2, -2, -1, -3, -3, -2, gapPenalty},
	/* Q */ {-1,  1,  0,  0, -3,  5,  2, -2,  0, -3, -2,  1,  0, -3, -1,  0, -1, -2, -1, -2,  0,  3, -1, gapPenalty},
	/* E */ {-1,  0,  0,  2, -4,  2,  5, -2,  0, -3, -3,  1, -2, -3, -1,  0, -1, -3, -2, -2,  1,  4, -1, gapPenalty},
	/* G */ { 0, -2,  0, -1, -3, -2, -2,  6, -2, -4, -4, -2, -3, -3, -2,  0, -2, -2, -3, -3, -1, -2, -1, gapPenalty},
	/* H */ {-2,  0,  1, -1, -3,  0,  0, -2,  8, -3, -3, -1, -2, -1, -2, -1, -2, -2,  2, -3,  0,  0, -1, gapPenalty},
	/* I */ {-1, -3, -3, -3, -1, -3, -3, -4, -3,  4,  2, -3,  1,  0, -3, -2, -1, -3, -1,  3, -3, -3, -1, gapPenalty},
	/* L */ {-1, -2, -3, -4, -1, -2, -3, -4, -3,  2,  4, -2,  2,  0, -3, -2, -1, -2, -1,  1, -4, -3, -1, gapPenalty},
	/* K */ {-1,  2,  0, -1, -3,  1,  1, -2, -1, -3, -2,  5, -1, -3, -1,  0, -1, -3, -2, -2,  0,  1, -1, gapPenalty},
	/* M */ {-1, -1, -2, -3, -1,  0, -2, -3, -2,  1,  2, -1,  5,  0, -2, -1, -1, -1, -1,  1, -3, -1, -1, gapPenalty},
	/* F */ {-2, -3, -3, -3, -2, -3, -3, -3, -1,  0,  0, -3,  0,  6, -4, -2, -2,  1,  3, -1, -3, -3, -1, gapPenalty},
	/* P */ {-1, -2, -2, -1, -3, -1, -1, -2, -2, -3, -3, -1, -2, -4,  7, -1, -1, -4, -3, -2, -2, -1, -2, gapPenalty},
	/* S */ { 1, -1,  1,  0, -1,  0,  0,  0, -1, -2, -2,  0, -1, -2, -1,  4,  1, -3, -2, -2,  0,  0,  0, gapPenalty},
	/* T */ { 0, -1,  0, -1, -1, -1, -1, -2, -2, -1, -1, -1, -1, -2, -1,  1,  5, -2, -2,  0, -1, -1,  0, gapPenalty},
	/* W */ {-3, -3, -4, -4, -2, -2, -3, -2, -2, -3, -2, -3, -1,  1, -4, -3, -2, 11,  2, -3, -4, -3, -2, gapPenalty},
	/* Y */ {-2, -2, -2, -3, -2, -1, -2, -3,  2, -1, -1, -2, -1,  3, -3, -2, -2,  2,  7, -1, -3, -2, -1, gapPenalty},
	/* V */ { 0, -3, -3, -3, -1, -2, -2, -3, -3,  3,  1, -2,  1, -1, -2, -2,  0, -3, -1,  4, -3, -2, -1, gapPenalty},
	/* B */ {-2, -1,  4,  4, -3,  0,  1, -1,  0, -3, -4,  0, -3, -3, -2,  0, -1, -4, -3, -3,  4,  1, -1, gapPenalty},
	/* Z */ {-1,  0,  0,  1, -3,  4,  4, -2,  0, -3, -3,  1, -1, -3, -1,  0, -1, -3, -2, -2,  1,  4, -1, gapPenalty},
	/* X */ { 0, -1, -1, -1, -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2,  0,  0, -2, -1, -1, -1, -1, -1, gapPenalty},
	/* - */ {gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, gapPenalty, 1}
	  };
	}


	  public Blosum62(int gapPenalty) {
		  super(gapPenalty);
	  }

	  public Blosum62() {
		  	super();
	  }

	   public int get(char c) {
	    switch (c) {
	      case 'A': return 0 ;
	      case 'R': return 1 ;
	      case 'N': return 2 ;
	      case 'D': return 3 ;
	      case 'C': return 4 ;
	      case 'Q': return 5 ;
	      case 'E': return 6 ;
	      case 'G': return 7 ;
	      case 'H': return 8 ;
	      case 'I': return 9 ;
	      case 'L': return 10;
	      case 'K': return 11;
	      case 'M': return 12;
	      case 'F': return 13;
	      case 'P': return 14;
	      case 'S': return 15;
	      case 'T': return 16;
	      case 'W': return 17;
	      case 'Y': return 18;
	      case 'V': return 19;
	      case 'B': return 20;
	      case 'Z': return 21;
	      case 'X': return 22;
	      case '-': return 23;
	      default: throw new RuntimeException("Invalid char: " + c) ;
	    }
	  }
 
}
