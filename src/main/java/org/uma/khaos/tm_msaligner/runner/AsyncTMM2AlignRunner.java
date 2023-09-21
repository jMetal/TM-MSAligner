package org.uma.khaos.tm_msaligner.runner;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.khaos.tm_msaligner.crossover.SPXMSACrossover;
import org.uma.khaos.tm_msaligner.mutation.ShiftClosedGapsMSAMutation;
import org.uma.khaos.tm_msaligner.parallel.algorithm.impl.AsynchronousMultiThreadedTM_M2Align;
import org.uma.khaos.tm_msaligner.problem.StandardTMMSAProblem;
import org.uma.khaos.tm_msaligner.problem.impl.MultiObjTMMSAProblem;
import org.uma.khaos.tm_msaligner.score.Score;
import org.uma.khaos.tm_msaligner.score.impl.AlignedSegment;
import org.uma.khaos.tm_msaligner.score.impl.SumOfPairsWithTopologyPredict;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Blosum62;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Phat;

public class AsyncTMM2AlignRunner {

  public static void main(String[] args) throws JMetalException, IOException {

    String refName = "msl" ;
    int numberOfTest = 1;

    int maxEvaluations = 5000 ;
    int populationSize = 50 ;
    int numberOfCores = 14;
    double probabilityCrossover=0.8;
    double probabilityMutation=0.2;
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

    String benchmarkPath = "data/benchmarks/ref7/" + refName + "/" ;
    String preComputedMSAPath = "data/precomputed_solutions/ref7/" +  refName + "/";
    String dataFile = benchmarkPath + refName + "_predicted_topologies.3line";

    String outputFolder = "data/pruebas/ref7/" + refName + "/test" + numberOfTest +"/" ;
    new File(outputFolder).mkdirs();


    List<String> preComputedFiles = new ArrayList<String>();
    preComputedFiles.add(preComputedMSAPath + refName + "kalign.fasta");
    preComputedFiles.add(preComputedMSAPath + refName + "mafft.fasta" );
    preComputedFiles.add(preComputedMSAPath + refName + "clustalw.fasta");
    preComputedFiles.add(preComputedMSAPath + refName + "muscle.fasta");
    preComputedFiles.add(preComputedMSAPath + refName + "t_coffee.fasta");
    preComputedFiles.add(preComputedMSAPath + refName + "tmt_coffee2023.fasta");
    //preComputedFiles.add(preComputedMSAPath + refName + "praline.fasta");

    StandardTMMSAProblem problem = new MultiObjTMMSAProblem(dataFile, scoreList,
        preComputedFiles,refName);

    var crossover = new SPXMSACrossover(probabilityCrossover);
    var mutation = new ShiftClosedGapsMSAMutation(probabilityMutation);

    Termination termination = new TerminationByEvaluations(maxEvaluations);

    long initTime = System.currentTimeMillis();

    AsynchronousMultiThreadedTM_M2Align tm_m2align =
        new AsynchronousMultiThreadedTM_M2Align(
            numberOfCores, problem, populationSize, crossover, mutation,
            termination);

    //var chartObserver = new FrontPlotTM_MSAObserver<>("", "SumOfPairsWithTopologyPredict",
    //    "AlignedSegment", problem.name(), 100);
    //tm_m2align.getObservable().register(chartObserver);

    tm_m2align.run();

    long endTime = System.currentTimeMillis();

    List<TM_MSASolution> population = tm_m2align.getResult();

    JMetalLogger.logger.info("Computing time: " + (endTime - initTime));

    for (TM_MSASolution solution : population) {
      for (int i = 0; i < problem.numberOfObjectives(); i++) {
        solution.objectives()[i] *= (scoreList.get(i).isAMinimizationScore() ? 1.0 : -1.0);
      }
    }

    //DefaultFileOutputContext funFile = new DefaultFileOutputContext(outputFolder + "FUN_" + refName + ".tsv");
    //funFile.setSeparator("\t");

    //SolutionListOutput slo = new SolutionListOutput(population);
    //slo.printObjectivesToFile(funFile, population);

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    System.exit(0) ;
  }
}
