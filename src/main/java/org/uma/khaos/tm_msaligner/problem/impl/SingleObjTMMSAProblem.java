package org.uma.khaos.tm_msaligner.problem.impl;

import org.uma.khaos.tm_msaligner.problem.StandardTMMSAProblem;
import org.uma.khaos.tm_msaligner.score.Score;
import org.uma.khaos.tm_msaligner.score.impl.AlignedSegment;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;
import org.uma.khaos.tm_msaligner.util.AA;

import java.io.IOException;
import java.util.List;

public class SingleObjTMMSAProblem  extends StandardTMMSAProblem {

    private final Score score ;

    public SingleObjTMMSAProblem(String msaProblemFileName, Score score,
                                 List<String> preComputedFiles) throws IOException {
        super(msaProblemFileName, preComputedFiles);

        setNumberOfObjectives(1);
        setName("Single Objective TM-MSA Problem");

        if (score.getName()=="AlignedSegments"){
            AlignedSegment scoreAS = (AlignedSegment)score;
            if (scoreAS.isNormalized()){
                scoreAS.setMaxSegmentAlignScore(MaxMinSegmentAlignScore[0]);
                scoreAS.setMinSegmentAlignScore(MaxMinSegmentAlignScore[1]);
            }
        }

        this.score = score ;
    }

    @Override
    public TM_MSASolution evaluate(TM_MSASolution solution) {
        solution.removeGapColumns();
        AA[][] decodedSequences = solution.decodeToMatrix();

        solution.objectives()[0] = score.compute(solution,decodedSequences) *
                (score.isAMinimizationScore()?1.0:-1.0);

        return solution ;
    }


}
