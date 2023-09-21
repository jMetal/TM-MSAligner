package org.uma.khaos.tm_msaligner.auto.runner;



import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;
import org.uma.khaos.tm_msaligner.auto.algorithm.ConfigurableTMMAligner;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;

public class ConfigurableTMMAlignerRunner {

  public static void main(String[] args) throws IOException {
    String instanceName = "msl" ;
    String referenceFrontFileName = "data/referenceFronts/" + instanceName + ".csv";

    String[] parameters =
        ("--problemName " + instanceName + " "
            + "--randomGeneratorSeed 23 "
            + "--referenceFrontFileName " + referenceFrontFileName + " "
            + "--maximumNumberOfEvaluations 25000 "
            + "--populationSize 50 "
            + "--algorithmResult population  "
            + "--offspringPopulationSize 100 "
            + "--variation crossoverAndMutationVariation "
            + "--crossover SPX "
            + "--crossoverProbability 0.8 "
            + "--mutation shiftClosedGaps "
            + "--mutationProbabilityFactor 1.0 "
            + "--selection tournament "
            + "--selectionTournamentSize 2 \n")
            .split("\\s+");

    var configurableAlgorithm = new ConfigurableTMMAligner();
    configurableAlgorithm.parse(parameters);

    ConfigurableTMMAligner.print(configurableAlgorithm.fixedParameterList());
    ConfigurableTMMAligner.print(configurableAlgorithm.configurableParameterList());

    EvolutionaryAlgorithm<TM_MSASolution> nsgaII = configurableAlgorithm.create();

    EvaluationObserver evaluationObserver = new EvaluationObserver(1000);
    RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
        new RunTimeChartObserver<>(
            "NSGA-II", 80, 1000,
            referenceFrontFileName, "F1", "F2");

    nsgaII.observable().register(evaluationObserver);
    nsgaII.observable().register(runTimeChartObserver);

    nsgaII.run();

    //JMetalLogger.logger.info("Total computing time: " + nsgaII.totalComputingTime()); ;

    new SolutionListOutput(nsgaII.result())
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();
  }
}
