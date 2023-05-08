package org.uma.khaos.tm_msaligner.runner;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.khaos.tm_msaligner.problem.StandardTMMSAProblem;
import org.uma.khaos.tm_msaligner.problem.impl.SingleObjTMMSAProblem;
import org.uma.khaos.tm_msaligner.score.Score;
import org.uma.khaos.tm_msaligner.score.impl.AlignedSegment;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;
import org.uma.khaos.tm_msaligner.util.AA;
import org.uma.khaos.tm_msaligner.util.AAArray;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Blosum62;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Phat;

import java.io.*;
import java.util.*;

public class test {

    public static void main(String[] args) throws IOException, CompoundNotFoundException {

        /*String refname = "photo"; //args[3]; ;
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
        Blosum62 blosum62Matrix = new Blosum62();

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



       /* String dataFile="C:\\TM-MSA\\GPCR\\gross-alignment.fasta";
        LinkedHashMap<String, ProteinSequence> sequences =
                    FastaReaderHelper.readFastaProteinSequence(new File(dataFile));

        DefaultFileOutputContext context = new DefaultFileOutputContext( "C:\\TM-MSA\\GPCR\\gross-alignmentwithougaps.fasta");
        context.setSeparator("\n");
        BufferedWriter bufferedWriter = context.getFileWriter();

        String alignment="";
        for (Map.Entry<String, ProteinSequence> entry : sequences.entrySet()) {
            alignment += ">" + entry.getValue().getOriginalHeader() + "\n";
            String seq = entry.getValue().getSequenceAsString();
            int compoundCount = 0;
            for (int j = 0; j < seq.length(); j++) {
                if (seq.charAt(j) != '-') {
                    alignment += seq.charAt(j);
                    compoundCount++;
                    if (compoundCount == 60) {
                        alignment += "\n";
                        compoundCount = 0;
                    }
                }
            }

            if (seq.length() % 60 != 0) {
                alignment += "\n";
            }
        }
        bufferedWriter.write(alignment);
        bufferedWriter.close();*/

       /* String dataFile="C:\\TM-MSA\\GPCR\\gross.fasta";
        LinkedHashMap<String, ProteinSequence> sequences =
                    FastaReaderHelper.readFastaProteinSequence(new File(dataFile));

        String line;
        BufferedReader br = new BufferedReader(new FileReader("C:\\TM-MSA\\GPCR\\TMRegions.csv"));
        line = br.readLine();

        DefaultFileOutputContext context = new DefaultFileOutputContext( "C:\\TM-MSA\\GPCR\\gross_predicted_topologies.3line");
        context.setSeparator("\n");
        BufferedWriter bufferedWriter = context.getFileWriter();

        String alignment="";
        String reg="";
        String[] fila;
        Integer ini, fin, pos, finnext;
        char tipo;
        for (Map.Entry<String, ProteinSequence> entry : sequences.entrySet()) {
            alignment += ">" + entry.getValue().getOriginalHeader() + "\n";
            alignment +=  entry.getValue().getSequenceAsString() + "\n";
            tipo='O';
            pos=1;
            if( (line = br.readLine()) != null) {
                reg = line;
                fila = reg.split(";");

                for(int i=2; i<15; i+=2) {
                    ini = Integer.parseInt(fila[i]);
                    fin = Integer.parseInt(fila[i+1]);
                    for(int j=pos; j<ini; j++)  alignment += tipo;
                    for(int j=ini; j<=fin; j++) alignment += "M";
                    pos=fin+1;
                    tipo = tipo=='O'?'I':'O';
                }

                for(int j=pos; j<=entry.getValue().getSequenceAsString().length(); j++) alignment += tipo;


            }else {
                System.out.print("Error No hay Regions para Seq " + entry.getValue().getOriginalHeader());
                System.exit(0);
                bufferedWriter.close();
            }

            alignment += "\n";
       }
        System.out.print(alignment);
        bufferedWriter.write(alignment);
        bufferedWriter.close();*/


        String path="C:\\TM-MSA\\pruebas\\NSGAII_2\\";
        String instance="nat";
        String filename="";
        String line;
        String[] datos;
        List<List<Float>> Frente = new ArrayList<List<Float>>();
        List<Float> punto;
        List<Float> puntoNuevo;
        Float X, Y;
        boolean added;

        for(int i=1; i<=10;i++){
            filename =path + instance + "\\FUN" + i +".tsv";
            BufferedReader br = new BufferedReader(new FileReader(filename));
            while( (line = br.readLine()) != null) {
                datos = line.split("\t");
                X = Float.parseFloat(datos[0]);
                Y = Float.parseFloat(datos[1]);
                added=false;
                for(int j=0; j<Frente.size(); j++){
                    punto = Frente.get(j);
                    if(Float.compare(X,punto.get(0)) > 0){
                        puntoNuevo = new ArrayList<>();
                        puntoNuevo.add(X); puntoNuevo.add(Y);
                        Frente.add(j,puntoNuevo);
                        added=true;
                        break;
                    }else
                        if(Float.compare(X,punto.get(0))==0){
                            if(Float.compare(Y,punto.get(1)) > 0){
                                puntoNuevo = new ArrayList<>();
                                puntoNuevo.add(X); puntoNuevo.add(Y);
                                Frente.add(j,puntoNuevo);
                                added=true;
                                break;
                            }
                            if(Float.compare(Y,punto.get(1)) ==0) { added=true; break;}
                         }
                }
               if(!added){
                   puntoNuevo = new ArrayList<>();
                   puntoNuevo.add(X); puntoNuevo.add(Y);
                   Frente.add(puntoNuevo);
               }
            }

        }



        DefaultFileOutputContext context = new DefaultFileOutputContext( path + instance + ".csv");
        context.setSeparator("\n");
        BufferedWriter bufferedWriter = context.getFileWriter();
        bufferedWriter.write("X,Y");
        for(int j=0; j<Frente.size(); j++) {

            bufferedWriter.write(String.format("%.0f",Frente.get(j).get(0))+ "," +
                                     String.format("%.0f",Frente.get(j).get(1)) + "\n");
            /*System.out.println(String.format("%.2f",Frente.get(j).get(0))+ "," +
                               String.format("%.2f",Frente.get(j).get(1)) );*/
        }
        bufferedWriter.close();

    }

}
