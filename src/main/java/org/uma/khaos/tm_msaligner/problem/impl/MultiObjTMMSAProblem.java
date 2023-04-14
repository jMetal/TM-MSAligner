package org.uma.khaos.tm_msaligner.problem.impl;


import org.uma.khaos.tm_msaligner.problem.StandardTMMSAProblem;
import org.uma.khaos.tm_msaligner.score.Score;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;
import org.uma.khaos.tm_msaligner.util.AA;

import java.io.IOException;
import java.util.List;

public class MultiObjTMMSAProblem extends StandardTMMSAProblem {


    private final List<Score> scoreList ;

    public MultiObjTMMSAProblem(String msaProblemFileName, List<Score> scoreList,
                                List<String> preComputedFiles) throws IOException {
        super(msaProblemFileName, preComputedFiles);

        setNumberOfObjectives(scoreList.size());
        setName("Multi Objective TM-MSA Problem");
        this.scoreList = scoreList ;
    }

    @Override
    public TM_MSASolution evaluate(TM_MSASolution solution) {
        solution.removeGapColumns();
        AA[][] decodedSequences = solution.decodeToMatrix();

       for (int i = 0 ; i < numberOfObjectives(); i++) {
            solution.objectives()[i] = scoreList.get(i).compute(solution,decodedSequences) *
                    (scoreList.get(i).isAMinimizationScore()?1.0:-1.0);
        }

        decodedSequences = null;
        return solution ;
    }


}
