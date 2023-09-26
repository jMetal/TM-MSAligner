package org.tm_msaligner.util.observer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.knowm.xchart.XYChart;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observer.Observer;
import org.uma.jmetal.util.plot.FrontScatterPlot;
import org.tm_msaligner.solution.TM_MSASolution;

public class FrontPlotTM_MSAObserver <S extends TM_MSASolution> implements Observer<Map<String, Object>> {
    private final FrontScatterPlot chart;
    private Integer evaluations;
    private final int plotUpdateFrequency;
    private String plotTitle;


    public FrontPlotTM_MSAObserver(String title, String xAxisTitle, String yAxisTitle, String legend, int plotUpdateFrequency) {
        this.chart = new FrontScatterPlot(title, xAxisTitle, yAxisTitle, legend);
        this.plotUpdateFrequency = plotUpdateFrequency;
        this.plotTitle = title;
    }

    public void update(Observable<Map<String, Object>> observable, Map<String, Object> data) {
        this.evaluations = (Integer)data.get("EVALUATIONS");
        List<S> population = (List)data.get("POPULATION");


        if (this.evaluations != null && population != null) {
            if (this.evaluations % this.plotUpdateFrequency == 0) {
                List<Double> objective1 = (List)population.stream().map((s) -> {
                    return s.objectives()[0]*-1.0;
                }).collect(Collectors.toList());
                List<Double> objective2 = (List)population.stream().map((s) -> {
                    return s.objectives()[1]*-1.0;
                }).collect(Collectors.toList());
                this.chart.chartTitle(this.plotTitle + ". Evaluations: " + this.evaluations);
                this.chart.updateChart(objective1, objective2);
            }
        } else {
            JMetalLogger.logger.warning(this.getClass().getName() + " : insufficient for generating real time information. Either EVALUATIONS or POPULATION keys have not been registered yet by the algorithm");
        }

    }

    public XYChart chart() {
        return this.chart.chart();
    }

}
