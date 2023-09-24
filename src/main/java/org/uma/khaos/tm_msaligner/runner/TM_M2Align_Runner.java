package org.uma.khaos.tm_msaligner.runner;


import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.Observer;
import org.uma.khaos.tm_msaligner.algorithm.multiobjective.TM_M2Align;
import org.uma.khaos.tm_msaligner.algorithm.multiobjective.TM_M2AlignBuilder;
import org.uma.khaos.tm_msaligner.mutation.ShiftClosedGapsMSAMutation;
import org.uma.khaos.tm_msaligner.problem.StandardTMMSAProblem;
import org.uma.khaos.tm_msaligner.problem.impl.MultiObjTMMSAProblem;
import org.uma.khaos.tm_msaligner.score.Score;
import org.uma.khaos.tm_msaligner.score.impl.AlignedSegment;
import org.uma.khaos.tm_msaligner.score.impl.SumOfPairsWithTopologyPredict;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;
import org.uma.khaos.tm_msaligner.util.observer.FrontPlotTM_MSAObserver;
import org.uma.khaos.tm_msaligner.util.observer.TM_MSAFitnessPlotObserver;
import org.uma.khaos.tm_msaligner.util.observer.TM_MSAFitnessWriteFileObserver;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Blosum62;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Phat;
import org.uma.khaos.tm_msaligner.util.visualization.MSAViewerHtmlMainPage;
import org.uma.khaos.tm_msaligner.util.visualization.MSAViewerHtmlPage;

import java.io.FileFilter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TM_M2Align_Runner extends AbstractAlgorithmRunner {

    public static void main(String[] args) throws JMetalException, IOException {

        //Parameters
        if (args.length != 6) {
            throw new JMetalException("Wrong number of arguments") ;
        }

        String dataDirectory = args[0]; // "data/custom_test/msl/"
        String problemName = args[1]; // "msl"
        Integer maxEvaluations = Integer.parseInt(args[2]);  //25000
        Integer populationSize = Integer.parseInt(args[3]); //100
        Integer numberOfCores = Integer.parseInt(args[4]);   //1
        //0: Ninguno 1: FitnessWriteFileObserver, 2: FitnessPlotObserver y 3: FrontPlotTM_MSAObserve
        int observerType = Integer.parseInt(args[5]);

        //Algorithm  Parameters
        double probabilityCrossover=0.8;
        double probabilityMutation=0.2;
        var weightGapOpenTM = 8;
        var weightGapExtendTM = 3;
        var weightGapOpenNonTM = 3;
        var weightGapExtendNonTM = 1;

        String dataFile = dataDirectory + problemName +"_predicted_topologies.3line";
        String outputFolder = dataDirectory + "results" + System.currentTimeMillis() + "/";
        if (!new File(outputFolder).mkdirs()){
            throw new JMetalException("Error creating Output Directory " + outputFolder) ;
        }
        List<String> preComputedFiles = getFastaFileNameListFromDir(dataDirectory);
        if(preComputedFiles.size()<2){
            throw new JMetalException("Wrong number of Pre-computed Alignments, Minimum 2 files are required") ;
        }

        List<Score> scoreList = new ArrayList<>();
        scoreList.add(new SumOfPairsWithTopologyPredict(
                new Phat(8),
                new Blosum62(),
                weightGapOpenTM,
                weightGapExtendTM,
                weightGapOpenNonTM,
                weightGapExtendNonTM));
        scoreList.add(new AlignedSegment());


        StandardTMMSAProblem problem = new MultiObjTMMSAProblem(dataFile, scoreList,
                                    preComputedFiles,problemName);

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

        int offspringPopulationSize = populationSize;
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
            if(observerType==1) {
                chartObserver = new TM_MSAFitnessWriteFileObserver(outputFolder + "BestScores.tsv", 100);
            } else if (observerType==2) {
                chartObserver = new TM_MSAFitnessPlotObserver("TM-M2Aligner solving Instance " + problemName ,
                        "Evaluations", scoreList.get(0).getName(), scoreList.get(0).getName(), 10, 0);
            }else
                chartObserver = new FrontPlotTM_MSAObserver<TM_MSASolution>("", "SumOfPairsWithTopologyPredict",
                        "AlignedSegment", problemName, 500);

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

        DefaultFileOutputContext funFile = new DefaultFileOutputContext(outputFolder + "FUN.tsv");
        funFile.setSeparator("\t");

        SolutionListOutput slo = new SolutionListOutput(population);
        slo.printObjectivesToFile(funFile, population);

        String pathLibsJS = "data/libs/";
        printMSAToFile(population, "resultsMSA.html",
                            "Pareto Front Solutions of MSA of Transmembrane Proteins for " + problemName + " dataset",
                            outputFolder,"FUN.tsv",
                            pathLibsJS);

       /* new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();*/
    }

    public static List<String> getFastaFileNameListFromDir(String dataDirectory){
        List<String> preComputedFiles = new ArrayList<>();

        File File_Directory = new File(dataDirectory);
        if (!(File_Directory.exists() && File_Directory.isDirectory())) {
            System.out.println(String.format(dataDirectory + " does not exist"));
            return preComputedFiles;
        }
        FileFilter Demo_Filefilter = new FileFilter() {
            public boolean accept(File Demo_File) {
                if (Demo_File.getName().endsWith(".fasta")) return true;
                return false;
            }
        };

        File[] Text_Files = File_Directory.listFiles(Demo_Filefilter);
        for (File Demo_File: Text_Files)
            preComputedFiles.add(dataDirectory + Demo_File.getName());


        return preComputedFiles;
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
