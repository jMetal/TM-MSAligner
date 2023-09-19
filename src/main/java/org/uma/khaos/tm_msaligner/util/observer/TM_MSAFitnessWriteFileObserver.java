package org.uma.khaos.tm_msaligner.util.observer;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.FileOutputContext;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observer.Observer;
import org.uma.jmetal.util.plot.SingleValueScatterPlot;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TM_MSAFitnessWriteFileObserver<S extends TM_MSASolution> implements Observer<Map<String, Object>> {

    private Integer evaluations ;
    private final int plotUpdateFrequency ;

    private String fileNamePath;

    /**
     * Constructor
     */
    public TM_MSAFitnessWriteFileObserver(String fileNamePath, int plotUpdateFrequency) throws IOException {

        this.plotUpdateFrequency = plotUpdateFrequency ;
        this.fileNamePath = fileNamePath;

        try {
                FileWriter fw = new FileWriter(fileNamePath);
                BufferedWriter writer = new BufferedWriter(fw);
                writer.write("Eval\tSOPwTP\tAlignedSegments\n");
                writer.close();
        } catch (IOException var7) {
            throw new JMetalException("Error printing objectives to file: ", var7);
        }



    }

    /**
     * This method displays a front (population)
     * @param data Map of pairs (key, value)
     */
    @Override
    public void update(Observable<Map<String, Object>> observable, Map<String, Object> data) {
        evaluations = (Integer)data.get("EVALUATIONS") ;
        List<S> population = (List)data.get("POPULATION");

        if (evaluations!=null && population!=null) {
            if (evaluations%plotUpdateFrequency == 0){

                List<Double> scores1 = (List)population.stream().map((s) -> {
                    return s.objectives()[0]*-1.0;
                }).collect(Collectors.toList());
                List<Double> scores2 = (List)population.stream().map((s) -> {
                    return s.objectives()[1]*-1.0;
                }).collect(Collectors.toList());

                try {
                    String textToAppend = evaluations + "\t"+
                            String.format("%.2f", Collections.max(scores1))  + "\t" +
                            String.format("%.2f", Collections.max(scores2));

                    try(FileWriter fw = new FileWriter(fileNamePath, true);
                        BufferedWriter writer = new BufferedWriter(fw);) {
                            writer.write(textToAppend);
                            writer.write( System.lineSeparator());
                        }

                } catch (IOException var7) {
                    throw new JMetalException("Error printing objectives to file: ", var7);
                }

            }
        } else {
            JMetalLogger.logger.warning(getClass().getName()+
                    " : insufficient for generating real time information." +
                    " Either EVALUATIONS or BEST_SOLUTION keys have not been registered yet by the algorithm");
        }
    }

    @Override
    public String toString() {
        return "FicheroValoresMaximos" ;
    }
}

