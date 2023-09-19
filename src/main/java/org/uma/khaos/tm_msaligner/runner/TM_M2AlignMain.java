package org.uma.khaos.tm_msaligner.runner;


import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.MultiThreadedEvaluation;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.khaos.tm_msaligner.algorithm.multiobjective.TM_M2Align;
import org.uma.khaos.tm_msaligner.algorithm.multiobjective.TM_M2AlignBuilder;
import org.uma.khaos.tm_msaligner.problem.StandardTMMSAProblem;
import org.uma.khaos.tm_msaligner.problem.impl.MultiObjTMMSAProblem;
import org.uma.khaos.tm_msaligner.score.Score;
import org.uma.khaos.tm_msaligner.score.impl.AlignedSegment;
import org.uma.khaos.tm_msaligner.score.impl.SumOfPairsWithTopologyPredict;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;
import org.uma.khaos.tm_msaligner.util.observer.FrontPlotTM_MSAObserver;
import org.uma.khaos.tm_msaligner.util.observer.TM_MSAFitnessWriteFileObserver;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Blosum62;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Phat;

public class TM_M2AlignMain extends AbstractAlgorithmRunner {

    public static void main(String[] args) throws JMetalException, IOException {

        /*if (args.length != 8) {
            throw new JMetalException("Wrong number of arguments") ;
        }

        Integer maxEvaluations = Integer.parseInt(args[0]);  //2500
        Integer populationSize = Integer.parseInt(args[1]); //100
        int offspringPopulationSize = populationSize;
        Integer numberOfCores = Integer.parseInt(args[2]);   //1
        String refname = args[3]; // "7tm";
        String benchmarkPath = args[4] + refname + "/"; //"C:\\TM-MSA\\ref7\\" + refname + "\\";
        String preComputedMSAPath = args[5] + refname + "/"; //"C:\\TM-MSA\\ref7\\" + refname + "\\";
        String PathOut = args[6] + refname + "/Ejec" + args[7] +"/"; //"C:\\TM-MSA\\pruebas\\NSGAII\\";*/

        String refName = "msl" ;
        int numberOfTest = 1;

        int maxEvaluations = 50000 ;
        int populationSize = 100 ;
        int offspringPopulationSize = populationSize ;
        int numberOfCores = 8;
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

        TM_M2Align tm_m2align = new TM_M2AlignBuilder(problem,
                            maxEvaluations,
                            populationSize,
                            offspringPopulationSize,
                            probabilityCrossover,
                            probabilityMutation,
                            numberOfCores).setEvaluation(new MultiThreadedEvaluation<>(numberOfCores, problem))
                            .build();


        //var chartObserver = new TM_MSAFitnessWriteFileObserver(outputFolder + "BestScores_" + refName + ".tsv",100);
               /*new TM_MSAFitnessPlotObserver("TM-M2Align solving " + refName  + " BAlibase Instance", "Evaluaciones",
                                              scoreList.get(0).getName(), scoreList.get(0).getName(), 10, 0);*/
        //var chartObserver = new FrontPlotTM_MSAObserver<TM_MSASolution>("", "SumOfPairsWithTopologyPredict", "AlignedSegment", problem.name(), 500);
        //tm_m2align.observable().register(chartObserver);

        tm_m2align.run();
        List<TM_MSASolution> population = tm_m2align.result();

        for (TM_MSASolution solution : population) {
            for (int i = 0; i < problem.numberOfObjectives(); i++) {
                solution.objectives()[i] *= (scoreList.get(i).isAMinimizationScore()?1.0:-1.0);
            }
        }

        JMetalLogger.logger.info("Total execution time : " + tm_m2align.totalComputingTime() + "ms");
        JMetalLogger.logger.info("Number of evaluations: " + tm_m2align.numberOfEvaluations()) ;

        DefaultFileOutputContext funFile = new DefaultFileOutputContext(outputFolder + "FUN_" + refName + ".tsv");
        funFile.setSeparator("\t");

        SolutionListOutput slo = new SolutionListOutput(population);
        slo.printObjectivesToFile(funFile, population);

        //printMSAToFile(population, outputFolder);

        new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();
    }
    public static void printMSAToFile(List<TM_MSASolution> solutionList, String PathOut) {

        try {
            for (int i = 0; i < solutionList.size(); i++) {
                DefaultFileOutputContext context = new DefaultFileOutputContext(PathOut + "MSASol" + i + ".fasta");
                context.setSeparator("\n");
                BufferedWriter bufferedWriter = context.getFileWriter();
                bufferedWriter.write(solutionList.get(i).toString());
                bufferedWriter.close();
            }

        } catch (IOException e) {
            throw new JMetalException("Error writing data ", e);
        }
    }
}
