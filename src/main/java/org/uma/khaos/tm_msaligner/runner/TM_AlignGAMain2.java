package org.uma.khaos.tm_msaligner.runner;

import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.khaos.tm_msaligner.algorithm.singleobjective.TM_AlignGA;
import org.uma.khaos.tm_msaligner.algorithm.singleobjective.TM_AlignGABuilder;
import org.uma.khaos.tm_msaligner.problem.StandardTMMSAProblem;
import org.uma.khaos.tm_msaligner.problem.impl.SingleObjTMMSAProblem;
import org.uma.khaos.tm_msaligner.problem.impl.TM_MSAProblemSOPwithSASingleObj;
import org.uma.khaos.tm_msaligner.score.Score;
import org.uma.khaos.tm_msaligner.score.impl.SumOfPairsWithTopologyPredict;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Blosum62;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Phat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TM_AlignGAMain2 extends AbstractAlgorithmRunner {

    public static void main(String[] args) throws JMetalException, IOException {

        if (args.length != 8) {
            throw new JMetalException("Wrong number of arguments") ;
        }

        Integer maxEvaluations = Integer.parseInt(args[0]);  //2500
        Integer populationSize = Integer.parseInt(args[1]); //100
        int offspringPopulationSize = populationSize;
        Integer numberOfCores = Integer.parseInt(args[2]);   //1
        String refname = args[3]; // "7tm";
        String benchmarkPath = args[4] + refname + "/"; //"C:\\TM-MSA\\ref7\\" + refname + "\\";
        String preComputedMSAPath = args[5] + refname + "/"; //"C:\\TM-MSA\\ref7\\" + refname + "\\";
        String PathOut = args[6] + refname + "/Ejec" + args[7] +"/"; //"C:\\TM-MSA\\pruebas\\NSGAII\\";


        String dataFile = benchmarkPath + refname + "_predicted_topologies.3line";

        List<String> preComputedFiles = new ArrayList<String>();
        //preComputedFiles.add(preComputedMSAPath + refname + "clustalw.msf.fasta" );
        preComputedFiles.add(preComputedMSAPath + refname + "kalign.msf.fasta");
        preComputedFiles.add(preComputedMSAPath + refname + "mafft.msf.fasta" );
        preComputedFiles.add(preComputedMSAPath + refname + "kalignP.msf.fasta");
        //preComputedFiles.add(preComputedMSAPath + refname + "muscle.msf.fasta");
        preComputedFiles.add(preComputedMSAPath + refname + "probcons.msf.fasta");
        preComputedFiles.add(preComputedMSAPath + refname + "t_coffee.msf.fasta");
        preComputedFiles.add(preComputedMSAPath + refname + "tmt_coffee2023.fasta");


        double weightGapOpenTM, weightGapExtendTM, weightGapOpenNonTM, weightGapExtendNonTM;
        weightGapOpenTM = 10;
        weightGapExtendTM = 3;
        weightGapOpenNonTM = 3;
        weightGapExtendNonTM = 1;

        StandardTMMSAProblem problem = new TM_MSAProblemSOPwithSASingleObj(dataFile,
                preComputedFiles,
                weightGapOpenTM,
                weightGapExtendTM,
                weightGapOpenNonTM,
                weightGapExtendNonTM);


        double probabilityCrossover=0.8;
        double probabilityMutation=0.2;

        TM_AlignGA tm_alignga = new TM_AlignGABuilder(problem,
                                maxEvaluations,
                                populationSize,
                                offspringPopulationSize,
                                probabilityCrossover,
                                probabilityMutation,
                                numberOfCores)
                                .build();

        tm_alignga.run();

        List<TM_MSASolution> population = tm_alignga.getResult();
        for (TM_MSASolution solution : population)
            solution.objectives()[0] *= -1.0;


        JMetalLogger.logger.info("Total execution time : " + tm_alignga.getTotalComputingTime()  + "ms");
        JMetalLogger.logger.info("Best found solution: " + population.get(0).objectives()[0]) ;

        File dir=new File(PathOut);
        if(dir.exists()) {
            DefaultFileOutputContext funFile = new DefaultFileOutputContext(PathOut + "FUN.tsv");
            funFile.setSeparator("\t");
            SolutionListOutput slo = new SolutionListOutput(population);
            slo.printObjectivesToFile(funFile, population);
            printMSAToFile(population, PathOut);
        }else{
            JMetalLogger.logger.info("Directory Results does not exist" ) ;
        }

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