package org.tm_msaligner.auto.runner;



import java.io.IOException;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;
import org.tm_msaligner.auto.algorithm.ConfigurableTMMAligner;
import org.tm_msaligner.solution.TM_MSASolution;

public class ConfigurableTMMAlignerWithDataRunner {

  public static void main(String[] args) throws IOException {
    String instanceName = "msl" ;
    String referenceFrontFileName = "data/referenceFronts/" + instanceName + ".csv";

    String[] parameters =
        ("--problemName " + instanceName + " "
            + "--randomGeneratorSeed 242 "
            + "--referenceFrontFileName " + referenceFrontFileName + " "
            + "--maximumNumberOfEvaluations 25000 "
            + "--populationSize 50 "
            + "--algorithmResult population  "
            + "--offspringPopulationSize 50 "
            + "--ranking dominanceRanking "
            + "--densityEstimator crowdingDistance "
            + "--variation crossoverAndMutationVariation "
            + "--crossover SPX "
            + "--crossoverProbability 0.9 "
            + "--mutation shiftClosedGaps "
            + "--mutationProbabilityFactor 1.5 "
            + "--selection tournament "
            + "--selectionTournamentSize 2 \n")
            .split("\\s+");

    var configurableAlgorithm = new ConfigurableTMMAligner();
    configurableAlgorithm.parse(parameters);

    ConfigurableTMMAligner.print(configurableAlgorithm.fixedParameterList());
    ConfigurableTMMAligner.print(configurableAlgorithm.configurableParameterList());

    EvolutionaryAlgorithm<TM_MSASolution> algorithm = configurableAlgorithm.create();

    EvaluationObserver evaluationObserver = new EvaluationObserver(1000);
    RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
        new RunTimeChartObserver<>(
            "NSGA-II", 80, 1000,
            referenceFrontFileName, "F1", "F2");

    algorithm.observable().register(evaluationObserver);
    algorithm.observable().register(runTimeChartObserver);

    algorithm.run();

    //JMetalLogger.logger.info("Total computing time: " + nsgaII.totalComputingTime()); ;

    new SolutionListOutput(algorithm.result())
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();
  }
}
