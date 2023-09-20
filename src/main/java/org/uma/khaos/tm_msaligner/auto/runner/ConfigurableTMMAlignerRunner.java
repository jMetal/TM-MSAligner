package org.uma.khaos.tm_msaligner.auto.runner;



import java.io.IOException;
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

    String referenceFrontFileName = "data/referenceFronts/msl.csv";

    String[] parameters =
        ("--problemName msl "
            + "--randomGeneratorSeed 12 "
            + "--referenceFrontFileName " + referenceFrontFileName + " "
            + "--maximumNumberOfEvaluations 25000 "
            + "--populationSize 100 "
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
            "NSGA-II", 80, 100,
            null, "F1", "F2");

    nsgaII.observable().register(evaluationObserver);
    nsgaII.observable().register(runTimeChartObserver);

    nsgaII.run();

    JMetalLogger.logger.info("Total computing time: " + nsgaII.totalComputingTime()); ;

    new SolutionListOutput(nsgaII.result())
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();
  }
}
