package org.tm_msaligner;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.tm_msaligner.util.observer.FrontPlotTM_MSAObserver;
import org.tm_msaligner.util.observer.TM_MSAFitnessPlotObserver;
import org.tm_msaligner.util.observer.TM_MSAFitnessWriteFileObserver;
import org.tm_msaligner.util.visualization.MSAViewerHtmlMainPage;
import org.tm_msaligner.util.visualization.MSAViewerHtmlPage;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.Observer;
import org.tm_msaligner.algorithm.multiobjective.TM_M2Align;
import org.tm_msaligner.algorithm.multiobjective.TM_M2AlignBuilder;
import org.tm_msaligner.mutation.ShiftClosedGapsMSAMutation;
import org.tm_msaligner.problem.StandardTMMSAProblem;
import org.tm_msaligner.problem.impl.MultiObjTMMSAProblem;
import org.tm_msaligner.score.Score;
import org.tm_msaligner.score.impl.AlignedSegment;
import org.tm_msaligner.score.impl.SumOfPairsWithTopologyPredict;
import org.tm_msaligner.solution.TM_MSASolution;
import org.tm_msaligner.util.substitutionmatrix.impl.Blosum62;
import org.tm_msaligner.util.substitutionmatrix.impl.Phat;

public class BAliBASETest extends AbstractAlgorithmRunner {

    public static void main(String[] args) throws JMetalException, IOException {

         if (args.length != 3) {
            throw new JMetalException("Wrong number of arguments") ;
        }
        String refName = args[0]; // "msl" ;
        //0: Ninguno 1: FitnessWriteFileObserver, 2: FitnessPlotObserver y 3: FrontPlotTM_MSAObserve
        int observerType = Integer.parseInt(args[1]);
        int frequencyObserver = Integer.parseInt(args[2]);

        int maxEvaluations = 25000 ;
        int populationSize = 50 ;
        int offspringPopulationSize = populationSize ;
        int numberOfCores = 8;
        double probabilityCrossover=0.8;
        double probabilityMutation=0.2;
        var weightGapOpenTM = 8;
        var weightGapExtendTM = 3;
        var weightGapOpenNonTM = 3;
        var weightGapExtendNonTM = 1;

        List<Score> scoreList = new ArrayList<>();
        scoreList.add(new SumOfPairsWithTopologyPredict(
                new Phat(8),
                new Blosum62(),
                weightGapOpenTM,
                weightGapExtendTM,
                weightGapOpenNonTM,
                weightGapExtendNonTM));
        scoreList.add(new AlignedSegment());

        String benchmarkPath = "resources/benchmarks/ref7/" + refName + "/" ;
        String preComputedMSAPath = "resources/precomputed_solutions/ref7/" +  refName + "/";
        String dataFile = benchmarkPath + refName + "_predicted_topologies.3line";


        String outputFolder = "resources/tests/ref7/" + refName + "/test" + System.currentTimeMillis() +"/" ;
        if (!new File(outputFolder).mkdirs()){
            throw new JMetalException("Error creating Output Directory " + outputFolder) ;
        }

        List<String> preComputedFiles = new ArrayList<String>();
        preComputedFiles.add(preComputedMSAPath + refName + "kalign.fasta");
        preComputedFiles.add(preComputedMSAPath + refName + "mafft.fasta" );
        //preComputedFiles.add(preComputedMSAPath + refName + "clustalw.fasta");
        //preComputedFiles.add(preComputedMSAPath + refName + "muscle.fasta");
        preComputedFiles.add(preComputedMSAPath + refName + "t_coffee.fasta");
        preComputedFiles.add(preComputedMSAPath + refName + "tmt_coffee2023.fasta");
        //preComputedFiles.add(preComputedMSAPath + refName + "praline.fasta");

        StandardTMMSAProblem problem = new MultiObjTMMSAProblem(dataFile, scoreList,
                                    preComputedFiles,refName);

        var mutationOperator = new ShiftClosedGapsMSAMutation(probabilityMutation) ;
        //var mutationOperator = new MergeAdjunctedGapsGroupsMSAMutation(probabilityMutation) ;
        //var mutationOperator = new InsertARandomGapMSAMutation(probabilityMutation) ;
        //var mutationOperator = new SplitANonGapsGroupMSAMutation(probabilityMutation) ;
        /*
        var mutationOperator = new MultipleMSAMutation(
            probabilityMutation,
            List.of(new ShiftClosedGapsMSAMutation(0.2),
                new MergeAdjunctedGapsGroupsMSAMutation(0.2),
                new SplitANonGapsGroupMSAMutation(0.2),
                new InsertARandomGapMSAMutation(0.2))) ;*/

        TM_M2Align tm_m2align = new TM_M2AlignBuilder(problem,
                            maxEvaluations,
                            populationSize,
                            offspringPopulationSize,
                            probabilityCrossover,
                            mutationOperator,
                            numberOfCores)
                            .build();


        if(observerType>=1 && observerType<=3){
            Observer chartObserver;
            if(frequencyObserver> maxEvaluations){
                throw new JMetalException("The frequency of the Observer can`t be greater than Maximun number of Evaluations") ;
            }

            if(observerType==1) {
                chartObserver = new TM_MSAFitnessWriteFileObserver(outputFolder + "BestScores_" + refName + ".tsv", frequencyObserver);
            } else if (observerType==2) {
                chartObserver = new TM_MSAFitnessPlotObserver("TM-M2Align solving " + refName + " BAliBASE Instance", "Evaluations",
                        scoreList.get(0).getName(), scoreList.get(0).getName(), frequencyObserver, 0);
            }else
                chartObserver = new FrontPlotTM_MSAObserver<TM_MSASolution>("", "SumOfPairsWithTopologyPredict",
                        "AlignedSegment", refName, frequencyObserver);

            tm_m2align.observable().register(chartObserver);
        }

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

        String pathLibsJS = "resources/libs/";
        printMSAToFile(population, "resultsMSA_" + refName + ".html",
                            "Solutions for BAliBASe Ref7 Instance " + refName,
                            outputFolder,"FUN_" + refName + ".tsv",
                            pathLibsJS);

       /* new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();*/
    }
    public static void printMSAToFile(List<TM_MSASolution> solutionList,
                                      String filenameHtml,
                                      String titulo,
                                      String PathOut,
                                      String filenameFUN,
                                      String pathLibsJS) {

        MSAViewerHtmlMainPage htmlPage =  new MSAViewerHtmlMainPage(titulo, filenameHtml,
                                          PathOut,  filenameFUN);
        htmlPage.save();
        for (int i = 0; i < solutionList.size(); i++) {
                MSAViewerHtmlPage msaHtml = new MSAViewerHtmlPage(
                        "MSASol" + i,
                        solutionList.get(i).toString(),
                        PathOut,
                        "MSASol" + i + ".html",
                        pathLibsJS );
                msaHtml.save();
        }

    }
}
