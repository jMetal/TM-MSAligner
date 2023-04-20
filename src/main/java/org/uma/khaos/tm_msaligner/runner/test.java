package org.uma.khaos.tm_msaligner.runner;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.uma.khaos.tm_msaligner.problem.StandardTMMSAProblem;
import org.uma.khaos.tm_msaligner.problem.impl.SingleObjTMMSAProblem;
import org.uma.khaos.tm_msaligner.score.Score;
import org.uma.khaos.tm_msaligner.score.impl.AlignedSegment;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;
import org.uma.khaos.tm_msaligner.util.AAArray;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Blosum62;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Phat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class test {

    public static void main(String[] args) throws IOException, CompoundNotFoundException {

        String refname = "photo"; //args[3]; ;
        String benchmarkPath = "C:\\TM-MSA\\benchmark\\ref7\\" + refname + "\\";
        String preComputedMSAPath = "C:\\TM-MSA\\Aligned\\ref7\\" + refname + "\\";

        String dataFile = benchmarkPath + refname + "_predicted_topologies.3line";

        List<String> preComputedFiles = new ArrayList<String>();
        //preComputedFiles.add(preComputedMSAPath + refname + "clustalw.msf.fasta" );
        preComputedFiles.add(preComputedMSAPath + refname + "kalign.msf.fasta");
        preComputedFiles.add(preComputedMSAPath + refname + "mafft.msf.fasta" );
        preComputedFiles.add(preComputedMSAPath + refname + "kalignP.msf.fasta");
        //preComputedFiles.add(preComputedMSAPath + refname + "muscle.msf.fasta");
        //preComputedFiles.add(preComputedMSAPath + refname + "probcons.msf.fasta");
        //preComputedFiles.add(preComputedMSAPath + refname + "t_coffee.msf.fasta");

        //preComputedFiles.add("C:\\TM-MSA\\pruebas\\NSGAII\\msl\\MSASol13.fasta");
        //preComputedFiles.add("C:\\TM-MSA\\pruebas\\NSGAII\\MSASol1.fasta");

       /* double weightGapOpenTM, weightGapExtendTM, weightGapOpenNonTM, weightGapExtendNonTM;
        weightGapOpenTM = 8;
        weightGapExtendTM = 3;
        weightGapOpenNonTM = 3;
        weightGapExtendNonTM = 1;

        Phat phatMatrix = new Phat(8);
        Blosum62 blosum62Matrix = new Blosum62();*/

        AlignedSegment score = new AlignedSegment(true);
        StandardTMMSAProblem problem = new SingleObjTMMSAProblem(dataFile, score,
                preComputedFiles);

        //problem.printConsoleoriginalSequences();


        for(int i=0; i<preComputedFiles.size();i++){
            List<AAArray> seqAligned = problem.readDataFromFastaFile(preComputedFiles.get(i));
            TM_MSASolution sol = new TM_MSASolution(seqAligned, problem);
            if (!sol.isValid())
                System.out.println("Sol MSA in file is not Valid");
            problem.evaluate(sol);
            System.out.println(preComputedFiles.get(i) + " sumAlignedSegments: " + String.format("%,.4f",sol.objectives()[0]*-1.0));
        }

        String Path ="C:\\TM-MSA\\pruebas\\NSGAII\\photo\\Ejec5";
        String file;
        for(int i=0; i<100;i++){
            file =Path + "\\MSASol" + i + ".fasta";
            List<AAArray> seqAligned = problem.readDataFromFastaFile(file);
            TM_MSASolution sol = new TM_MSASolution(seqAligned, problem);
            if (!sol.isValid())
                System.out.println("Sol MSA in file is not Valid");
            problem.evaluate(sol);
            System.out.println(file + " sumAlignedSegments: " + String.format("%,.4f",sol.objectives()[0]*-1.0));
        }


        /*System.out.println("MSA2");
        TMMSASolution child = new TMMSASolution(sol);
        if (!child.isValid())
            System.out.println("child MSA in file is not Valid");

        System.out.print(child.toString());*/






    }
}
