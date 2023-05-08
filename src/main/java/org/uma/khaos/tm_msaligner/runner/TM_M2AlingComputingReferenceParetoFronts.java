package org.uma.khaos.tm_msaligner.runner;

import org.uma.jmetal.component.algorithm.multiobjective.NSGAIIBuilder;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.ExperimentBuilder;
import org.uma.jmetal.lab.experiment.component.impl.*;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.khaos.tm_msaligner.algorithm.multiobjective.TM_M2Align;
import org.uma.khaos.tm_msaligner.algorithm.multiobjective.TM_M2AlignBuilder;
import org.uma.khaos.tm_msaligner.problem.StandardTMMSAProblem;
import org.uma.khaos.tm_msaligner.problem.impl.MultiObjTMMSAProblem;
import org.uma.khaos.tm_msaligner.score.Score;
import org.uma.khaos.tm_msaligner.score.impl.AlignedSegment;
import org.uma.khaos.tm_msaligner.score.impl.SumOfPairsWithTopologyPredict;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Blosum62;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Phat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TM_M2AlingComputingReferenceParetoFronts {

    private static final int INDEPENDENT_RUNS = 10;

    public static void main(String[] args) throws IOException {

        String experimentBaseDirectory = "C:\\TM-MSA\\pruebas\\";

        List<ExperimentProblem<TM_MSASolution>> problemList = new ArrayList<>();

        String refname = "ptga";
        String benchmarkPath = "C:\\TM-MSA\\benchmark\\ref7\\" + refname + "\\";
        String preComputedMSAPath = "C:\\TM-MSA\\Aligned\\ref7\\" + refname + "\\";

        double weightGapOpenTM, weightGapExtendTM, weightGapOpenNonTM, weightGapExtendNonTM;
        weightGapOpenTM = 8;
        weightGapExtendTM = 3;
        weightGapOpenNonTM = 3;
        weightGapExtendNonTM = 1;

        List<Score> scoreList = new ArrayList<>();
        scoreList.add(new SumOfPairsWithTopologyPredict(
                new Phat(8),
                new Blosum62(),
                weightGapOpenTM,
                weightGapExtendTM,
                weightGapOpenNonTM,
                weightGapExtendNonTM));
        scoreList.add(new AlignedSegment());

        String dataFile = benchmarkPath + refname + "_predicted_topologies.3line";

        List<String> preComputedFiles = new ArrayList<String>();
        preComputedFiles.add(preComputedMSAPath + refname + "kalign.fasta");
        preComputedFiles.add(preComputedMSAPath + refname + "mafft.fasta" );
        preComputedFiles.add(preComputedMSAPath + refname + "clustalw.fasta");
        //preComputedFiles.add(preComputedMSAPath + refname + "muscle.fasta");
        //preComputedFiles.add(preComputedMSAPath + refname + "t_coffee.fasta");
        //preComputedFiles.add(preComputedMSAPath + refname + "tmt_coffee2023.fasta");
        //preComputedFiles.add(preComputedMSAPath + refname + "praline.fasta");

        MultiObjTMMSAProblem problem = new MultiObjTMMSAProblem(dataFile, scoreList,
                preComputedFiles, refname);


        problemList.add(new ExperimentProblem<>(problem));


        List<ExperimentAlgorithm<TM_MSASolution, List<TM_MSASolution>>> algorithmList =
                configureAlgorithmList(problemList);

        Experiment<TM_MSASolution, List<TM_MSASolution>> experiment =
                new ExperimentBuilder<TM_MSASolution, List<TM_MSASolution>>("NSGAII_2")
                        .setAlgorithmList(algorithmList)
                        .setProblemList(problemList)
                        .setExperimentBaseDirectory(experimentBaseDirectory)
                        .setOutputParetoFrontFileName("FUN")
                        .setOutputParetoSetFileName("VAR")
                        .setReferenceFrontDirectory(experimentBaseDirectory + "/NSGAII_2/referenceFronts")
                        .setIndicatorList(Arrays.asList(
                                new Epsilon(),
                                new Spread(),
                                new GenerationalDistance(),
                                new PISAHypervolume(),
                                new NormalizedHypervolume(),
                                new InvertedGenerationalDistance(),
                                new InvertedGenerationalDistancePlus()))
                        .setIndependentRuns(INDEPENDENT_RUNS)
                        .setNumberOfCores(8)
                        .build();

        //new ExecuteAlgorithms<>(experiment).run();
        new GenerateReferenceParetoSetAndFrontFromDoubleSolutions(experiment).run();
        new ComputeQualityIndicators<>(experiment).run();
        new GenerateLatexTablesWithStatistics(experiment).run();
        new GenerateFriedmanHolmTestTables<>(experiment).run();
        new GenerateWilcoxonTestTablesWithR<>(experiment).run();
        new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(2).run();
        //new GenerateHtmlPages<>(experiment).run() ;
    }

    /**
     * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of
     * a {@link ExperimentAlgorithm}, which is a decorator for class {@link Algorithm}. The {@link
     * ExperimentAlgorithm} has an optional tag component, that can be set as it is shown in this
     * example, where four variants of a same algorithm are defined.
     */
    static List<ExperimentAlgorithm<TM_MSASolution, List<TM_MSASolution>>> configureAlgorithmList(
            List<ExperimentProblem<TM_MSASolution>> problemList) {

        List<ExperimentAlgorithm<TM_MSASolution, List<TM_MSASolution>>> algorithms = new ArrayList<>();
        for (int run = 0; run < INDEPENDENT_RUNS; run++) {
            for (ExperimentProblem<TM_MSASolution> experimentProblem : problemList) {

                TM_M2Align algorithm = new TM_M2AlignBuilder(
                        (MultiObjTMMSAProblem)experimentProblem.getProblem(),
                        25000,
                        100,
                        100,
                        0.8,
                        0.2,
                        8)
                        .build();


                algorithms.add(new ExperimentAlgorithm<>(algorithm, "TM_M2Align", experimentProblem, run));
            }


        }
        return algorithms;
    }

}
