package org.uma.khaos.tm_msaligner.solution;

import org.uma.jmetal.solution.AbstractSolution;
import org.uma.khaos.tm_msaligner.problem.StandardTMMSAProblem;

import org.uma.khaos.tm_msaligner.util.AA;
import org.uma.khaos.tm_msaligner.util.AAArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TM_MSASolution extends AbstractSolution<List<Integer>> {

  private char[][] decodedSolution;
  private final StandardTMMSAProblem problem ;

  /** Constructor */
  public TM_MSASolution(StandardTMMSAProblem problem) {
    super(problem.numberOfVariables(), problem.numberOfObjectives()) ;

    this.problem = problem ;
    setAttributesSeqName(problem.getListOfSequenceNames());
    decodedSolution = null;
  }

  public TM_MSASolution(List<AAArray> AlignedSeqs, StandardTMMSAProblem problem) {
    super(problem.numberOfVariables(), problem.numberOfObjectives()) ;
    this.problem = problem ;
    setAttributesSeqName(problem.getListOfSequenceNames());
    encode(AlignedSeqs);
  }

  /** Constructor */
  public TM_MSASolution(StandardTMMSAProblem problem, List<List<Integer>> gapsGroups) {
    super(problem.numberOfVariables(), problem.numberOfObjectives()) ;

    this.problem = problem ;

    for (int i = 0; i < problem.numberOfVariables(); i++) {
      variables().set(i, gapsGroups.get(i));
    }

    setAttributesSeqName(problem.getListOfSequenceNames());
  }

  /** Copy Constructor */
  public TM_MSASolution(TM_MSASolution solution) {
    super(solution.variables().size(), solution.objectives().length, solution.constraints().length);
    problem = solution.problem ;
    for (int i = 0; i < variables().size(); i++) {

      List<Integer> gapsGroup = new ArrayList();
      for (int j = 0; j < solution.variables().get(i).size(); j++)
        gapsGroup.add(solution.variables().get(i).get(j));

      variables().set(i, gapsGroup);
    }

    for (int i = 0; i < solution.objectives().length; i++) {
      objectives()[i] = solution.objectives()[i];
    }

    attributes = new HashMap(solution.attributes);


  }


  public void encode(List<AAArray> alignedSequences) {
    for (int i = 0; i < alignedSequences.size(); i++) {
      variables().set(i, getGapsGroup(alignedSequences.get(i)));
    }
  }

  public List<Integer> getGapsGroup(AAArray sequence) {
    List<Integer> gapsGroups = new ArrayList<Integer>();
    boolean GapOpen = false;
    int start = 0;
    for (int i = 0; i < sequence.getSize(); i++) {
      if (sequence.charAt(i) == '-') {
        if (!GapOpen) {
          GapOpen = true;
          start = i;
        }
      } else {
        if (GapOpen) {
          gapsGroups.add(start);
          gapsGroups.add(i - 1);
          GapOpen = false;
        }
      }
    }

    if (GapOpen) {
      gapsGroups.add(start);
      gapsGroups.add(sequence.getSize() - 1);
    }

    return gapsGroups;
  }

  public AA[][] decodeToMatrix() {
    return decodeToMatrix(getOriginalSequences());
  }

  public AA[][] decodeToMatrix(List<AAArray> originalSeqs) {

    AA[][] alignedSequences = new AA[variables().size()][];

    for (int i = 0; i < variables().size(); i++) {
      alignedSequences[i] =
          decodeOneSequenceToArray(
              originalSeqs.get(i).getCharArray(), variables().get(i), getAlignmentLength(i));
    }

    return alignedSequences;
  }

  public AA[] decodeOneSequenceToArray(AA[] OriginalSeq, List<Integer> gapsGroups, int size) {
    AA[] alignedSequence = new AA[size];
    int i, g, k, l;
    i = 0;
    g = 0;
    k = 0;
    while (i < size) {
      if (g < gapsGroups.size()) {
        if (i < gapsGroups.get(g)) {
          alignedSequence[i++] = OriginalSeq[k++];
        } else {
          for (l = gapsGroups.get(g); l <= gapsGroups.get(g + 1); l++) {
            alignedSequence[l] = new AA(AA.GAP_IDENTIFIER,
                    OriginalSeq[k > 0 ? k-1 : 0].getType(),
                    OriginalSeq[k < OriginalSeq.length ? k : k-1].getType());
          }
          i += gapsGroups.get(g + 1) - gapsGroups.get(g) + 1;
          g += 2;
        }
      } else {
        alignedSequence[i++] = OriginalSeq[k++];
      }
    }

    return alignedSequence;
  }

  public void mergeGapsGroups() {
    for (int i = 0; i < variables().size(); i++) {
      for (int j = 1; j < variables().get(i).size() - 2; j += 2) {

        if (variables().get(i).get(j) + 1 == variables().get(i).get(j + 1)) {
          variables().get(i).set(j, variables().get(i).get(j + 2));
          variables().get(i).remove(j + 1);
          variables().get(i).remove(j + 1);
          j -= 2;
        }
      }
    }
  }

  public void removeGapColumns() {
    List<Integer> gapsGroups;
    List<Integer> gapColumnsIndex = new ArrayList();
    List<Integer> gapColumnsIndexGG;
    int j, k;

    gapsGroups = variables().get(0);
    for (j = 0; j < gapsGroups.size() - 1; j += 2) {
      gapColumnsIndexGG = getGapsColumns(1, gapsGroups.get(j), gapsGroups.get(j + 1));

      for (k = 0; k < gapColumnsIndexGG.size(); k++) {
        gapColumnsIndex.add(gapColumnsIndexGG.get(k));
      }
    }

    for (j = 0; j < gapColumnsIndex.size(); j++) {
      removeGapColumn(gapColumnsIndex.get(j) - j);
    }
  }

  private List<Integer> getGapsColumns(int varIndex, int RMin, int RMax) {
    List<Integer> gapsGroups;
    List<Integer> gapColumnsIndexGG;
    List<Integer> gapColumnsIndex = new ArrayList();
    int j, k, RMinAux, RMaxAux;
    int numberOfVariables = this.variables().size();

    gapsGroups = variables().get(varIndex);
    for (j = 0; j < gapsGroups.size() - 1; j += 2) {

      if (RMax < gapsGroups.get(j)) break;

      RMinAux = RMin > gapsGroups.get(j) ? RMin : gapsGroups.get(j);
      RMaxAux = RMax < gapsGroups.get(j + 1) ? RMax : gapsGroups.get(j + 1);

      if (RMinAux <= RMaxAux) { // GapsIntersection
        if (varIndex == numberOfVariables - 1) { // last sequence into the GapsRange
          gapColumnsIndex.add(RMinAux);
          gapColumnsIndex.add(RMaxAux);
        } else {

          gapColumnsIndexGG = getGapsColumns(varIndex + 1, RMinAux, RMaxAux);

          if (varIndex == numberOfVariables - 2) {
            if (gapColumnsIndexGG.size() > 0) {
              for (k = gapColumnsIndexGG.get(0); k <= gapColumnsIndexGG.get(1); k++) {
                gapColumnsIndex.add(k);
              }
            }
          } else {
            for (k = 0; k < gapColumnsIndexGG.size(); k++) {
              gapColumnsIndex.add(gapColumnsIndexGG.get(k));
            }
          }
        }
      }
    }

    return gapColumnsIndex;
  }

  public void removeGapColumn(int index) {

    List<Integer> gapsGroups;
    int i, j, k;
    for (i = 0; i < this.variables().size(); i++) {
      gapsGroups = this.variables().get(i);

      for (j = 0; j < gapsGroups.size() - 1; j += 2) {
        if (index >= gapsGroups.get(j) && index <= gapsGroups.get(j + 1)) {
          for (k = j + 1; k < gapsGroups.size(); k++) {
            gapsGroups.set(k, gapsGroups.get(k) - 1);
          }
          if (gapsGroups.get(j) > gapsGroups.get(j + 1)) {
            gapsGroups.remove(j);
            gapsGroups.remove(j);
          }

          break;
        }
      }
    }
  }

  public boolean isGapColumn(int index) {
    List<Integer> gapsGroups;
    boolean gapColumn;
    int i, j;
    for (i = 0; i < variables().size(); i++) {
      gapsGroups = variables().get(i);
      gapColumn = false;
      for (j = 0; j < gapsGroups.size() - 1; j += 2) {
        if (index >= gapsGroups.get(j) && index <= gapsGroups.get(j + 1)) {
          gapColumn = true;
          break;
        }
      }

      if (!gapColumn) {
        return false;
      }
    }
    return true;
  }

  public String getVariableValueString(int i) {

    String Sequence;

    Sequence =
        ">"
            + attributes.get("SeqName" + i).toString()
            + "\n"
            + decodeOneSequenceToArray(
                    getOriginalSequences().get(i).getCharArray(),
                    variables().get(i),
                    getAlignmentLength(i))
                .toString();

    if (i >= this.variables().size() - 1) Sequence += "\n";

    return Sequence;
  }

  public List<Integer> getVariableListInteger(int i) {
    return variables().get(i);
  }

  public List<AAArray> getOriginalSequences() {
    return this.problem.originalSequences;
  }

  public StandardTMMSAProblem getMSAProblem() {
    return this.problem;
  }

  public boolean isValid() {
    int sizeAlignment = getAlignmentLength();
    for (int k = 1; k < variables().size(); k++) {
      if (sizeAlignment != getOriginalSequences().get(k).getSize() + getNumberOfGaps(k)) {

        System.out.println(
            "Error Solution, "
                + k
                + " sequence has a wrong Length (OSeqLenghth: "
                + getOriginalSequences().get(k).getSize()
                + ") + NumOfGaps: "
                + getNumberOfGaps(k)
                + " is not equal to Size: "
                + sizeAlignment);
        return false;
      }
    }

    return true;
  }

  public TM_MSASolution copy() {
    return new TM_MSASolution(this);
  }

  /*
   * Returns the number of gaps of a sequence
   * @param index
   * @return
   */
  public int getNumberOfGaps() {

    int numberOfGaps = 0;
    int i;
    for (i = 0; i < this.variables().size(); i++) {
      numberOfGaps += getNumberOfGaps(i);
    }
    return numberOfGaps;
  }

  public int getNumberOfGaps(Integer index) {

    int numberOfGaps = 0;
    int j;
    List<Integer> gapsGroup = this.variables().get(index);
    for (j = 0; j < gapsGroup.size(); j += 2) {
      numberOfGaps += gapsGroup.get(j + 1) - gapsGroup.get(j) + 1;
    }
    return numberOfGaps;
  }

  /** Set The Attributes with the sequence name list */
  public void setAttributesSeqName(List<StringBuilder> SeqNames) {
    for (int i = 0; i < variables().size(); i++) {
      if (i < SeqNames.size()) this.attributes().put("SeqName" + i, SeqNames.get(i).toString());
      else this.attributes().put("SeqName" + i, "");
    }
  }

  @Override
  public String toString() {
    AA[][] sequences = decodeToMatrix();
    String alignment = "";

    for (int i = 0; i < variables().size(); i++) {
      alignment += ">" + attributes().get("SeqName" + i).toString() + "\n";
      int compoundCount = 0;
      for (int j = 0; j < sequences[i].length; j++) {
        alignment += sequences[i][j].toString();
        compoundCount++;
        if (compoundCount == 60) {
          alignment += "\n";
          compoundCount = 0;
        }
      }

      if (sequences[i].length % 60 != 0) {
        alignment += "\n";
      }

     /* compoundCount = 0;
      for (int j = 0; j < sequences[i].length; j++) {
        alignment += sequences[i][j].getType().getAbrev();
        compoundCount++;
        if (compoundCount == 60) {
         // alignment += "\n";
          compoundCount = 0;
        }
      }

      if (sequences[i].length % 60 != 0) {
        alignment += "\n";
      }*/

    }

    return alignment;
  }

  /** Write the MultipleSequenceAlignmentSolution in Fasta format */
  /* public void printSolutionToFasta(String fileName) throws Exception {

    List<ProteinSequence> proteinSequenceList = convertSolutionToProteinSequenceList();
    FastaWriterHelper.writeProteinSequence(new File(fileName), proteinSequenceList);
  }*/

  /** Create a Protein sequence list from the sequences stored in the variables */
  /*public List<ProteinSequence> convertSolutionToProteinSequenceList() throws Exception {

    List<ProteinSequence> proteinSequenceList = new ArrayList<ProteinSequence>(getNumberOfVariables());
    List<AAArray> Seqs = decode();

    for (int i = 0; i < getNumberOfVariables(); i++) {
      proteinSequenceList.add(new ProteinSequence(Seqs.get(i).toString()));
      proteinSequenceList.get(i).setOriginalHeader(getAttribute("SeqName" + i).toString());
    }

    return proteinSequenceList;
  }*/

  public int getAlignmentLength(int varIndex) {
      return problem.getSizeOfOriginalSequence(varIndex) + getNumberOfGaps(varIndex);
  }

  public int getAlignmentLength() {
    return problem.getSizeOfOriginalSequence(0) + getNumberOfGaps(0);
  }


}
