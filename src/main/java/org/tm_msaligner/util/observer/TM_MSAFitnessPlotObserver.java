package org.tm_msaligner.util.observer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observer.Observer;
import org.uma.jmetal.util.plot.SingleValueScatterPlot;
import org.tm_msaligner.solution.TM_MSASolution;

public class TM_MSAFitnessPlotObserver<S extends TM_MSASolution> implements Observer<Map<String, Object>> {
    private final SingleValueScatterPlot chart;
    private Integer evaluations ;
    private final int plotUpdateFrequency ;
    private String valueName ;
    private String plotTitle ;

    private int numberOfObjectiveToPlot;

    /**
     * Constructor
     */
    public TM_MSAFitnessPlotObserver(String title, String xAxisTitle, String yAxisTitle, String valueName,
                               int plotUpdateFrequency, int numberOfObjectiveToPlot) {
        chart = new SingleValueScatterPlot(title, xAxisTitle, yAxisTitle, valueName) ;
        this.plotUpdateFrequency = plotUpdateFrequency ;
        this.valueName = valueName ;
        this.plotTitle = title ;
        this.numberOfObjectiveToPlot = numberOfObjectiveToPlot;
        chart.chartTitle(title);
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

                List<Double> scores = (List)population.stream().map((s) -> {
                    return s.objectives()[numberOfObjectiveToPlot]*-1.0;
                }).collect(Collectors.toList());
                chart.updateChart(evaluations, Collections.max(scores));
            }
        } else {
            JMetalLogger.logger.warning(getClass().getName()+
                    " : insufficient for generating real time information." +
                    " Either EVALUATIONS or BEST_SOLUTION keys have not been registered yet by the algorithm");
        }
    }

    @Override
    public String toString() {
        return valueName ;
    }
}

