package org.uma.khaos.tm_msaligner.runner;


import static org.uma.khaos.tm_msaligner.runner.TM_AlignGAMain2.printMSAToFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.NSGAIIBuilder;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.MultiThreadedEvaluation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.FrontPlotObserver;
import org.uma.khaos.tm_msaligner.crossover.SPXMSACrossover;
import org.uma.khaos.tm_msaligner.mutation.ShiftClosedGapsMSAMutation;
import org.uma.khaos.tm_msaligner.problem.StandardTMMSAProblem;
import org.uma.khaos.tm_msaligner.problem.impl.MultiObjTMMSAProblem;
import org.uma.khaos.tm_msaligner.score.Score;
import org.uma.khaos.tm_msaligner.score.impl.AlignedSegment;
import org.uma.khaos.tm_msaligner.score.impl.SumOfPairsWithTopologyPredict;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Blosum62;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Phat;

public class NSGIIRunner {

  public static void main(String[] args) throws JMetalException, IOException {
    /*
    if (args.length != 8) {
      throw new JMetalException("Wrong number of arguments");
    }

    Integer maxEvaluations = Integer.parseInt(args[0]);  //2500
    Integer populationSize = Integer.parseInt(args[1]); //100
    int offspringPopulationSize = populationSize;
    Integer numberOfCores = Integer.parseInt(args[2]);   //1
    String refname = args[3]; // "7tm";
    String benchmarkPath = args[4] + refname + "/"; //"C:\\TM-MSA\\ref7\\" + refname + "\\";
    String preComputedMSAPath = args[5] + refname + "/"; //"C:\\TM-MSA\\ref7\\" + refname + "\\";
    String PathOut = args[6] + refname + "/Ejec" + args[7] + "/"; //"C:\\TM-MSA\\pruebas\\NSGAII\\";
   */
    int maxEvaluations = 2500 ;
    int populationSize = 100 ;
    int offspringPopulationSize = populationSize ;
    int numberOfCores = 1 ;
    String refName = "dtd" ;
    String benchmarkPath = "data/benchmarks/ref7/" + refName + "/" ;
    String preComputedMSAPath = "data/precomputed_solutions/ref7/" +  refName + "/";
    String outputFolder = "data/pruebas/ref7/" + refName + "/" ;

    double probabilityCrossover = 0.8;
    double probabilityMutation = 0.2;

    double weightGapOpenTM = 8;
    double weightGapExtendTM = 3;
    double weightGapOpenNonTM = 3;
    double weightGapExtendNonTM = 1;

    List<Score> scoreList = new ArrayList<>();
    scoreList.add(new SumOfPairsWithTopologyPredict(
        new Phat(8),
        new Blosum62(),
        weightGapOpenTM,
        weightGapExtendTM,
        weightGapOpenNonTM,
        weightGapExtendNonTM));
    scoreList.add(new AlignedSegment());

    String dataFile = benchmarkPath + refName + "_predicted_topologies.3line";

    List<String> preComputedFiles = new ArrayList<String>();
    preComputedFiles.add(preComputedMSAPath + refName + "kalign.fasta");
    preComputedFiles.add(preComputedMSAPath + refName + "mafft.fasta");
    //preComputedFiles.add(preComputedMSAPath + refName + "clustalw.fasta");
    //preComputedFiles.add(preComputedMSAPath + refName + "muscle.fasta");
    preComputedFiles.add(preComputedMSAPath + refName + "t_coffee.fasta");
    //preComputedFiles.add(preComputedMSAPath + refname + "tmt_coffee2023.fasta");
    //preComputedFiles.add(preComputedMSAPath + refname + "praline.fasta");

    StandardTMMSAProblem problem = new MultiObjTMMSAProblem(dataFile, scoreList,
        preComputedFiles, refName);

    var crossover = new SPXMSACrossover(probabilityCrossover);
    var mutation = new ShiftClosedGapsMSAMutation(probabilityMutation);

    Termination termination = new TerminationByEvaluations(maxEvaluations);

    EvolutionaryAlgorithm<TM_MSASolution> nsgaII = new NSGAIIBuilder<>(
        problem,
        populationSize,
        offspringPopulationSize,
        crossover,
        mutation)
        .setTermination(termination)
        .setEvaluation(new MultiThreadedEvaluation<>(numberOfCores, problem))
        .build();


    var chartObserver =
        new FrontPlotObserver<DoubleSolution>("NSGA-II", "F1", "F2", problem.name(), 500);
    nsgaII.observable().register(chartObserver);

    nsgaII.run();

    List<TM_MSASolution> population = nsgaII.result();

    for (TM_MSASolution solution : population) {
      for (int i = 0; i < problem.numberOfObjectives(); i++) {
        solution.objectives()[i] *= (scoreList.get(i).isAMinimizationScore() ? 1.0 : -1.0);
      }
    }

    JMetalLogger.logger.info("Total execution time : " + nsgaII.totalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + nsgaII.numberOfEvaluations());

    DefaultFileOutputContext funFile = new DefaultFileOutputContext(outputFolder + "FUN.tsv");
    funFile.setSeparator("\t");

    SolutionListOutput slo = new SolutionListOutput(population);
    slo.printObjectivesToFile(funFile, population);

    printMSAToFile(population, outputFolder);
  }
}
