package org.tm_msaligner.auto.runner;



import java.io.IOException;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;
import org.tm_msaligner.auto.algorithm.ConfigurableTMMAligner;
import org.tm_msaligner.solution.TM_MSASolution;

public class ConfigurableTMMAlignerWithExternalArchiveRunner {

  public static void main(String[] args) throws IOException {

    String instanceName = "msl" ;
    String referenceFrontFileName = "data/referenceFronts/" + instanceName + ".csv";

    String[] parameters =
        ("--problemName " + instanceName + " "
            + "--randomGeneratorSeed 234 "
            + "--referenceFrontFileName " + referenceFrontFileName + " "
            + "--maximumNumberOfEvaluations 20000 "
            + "--populationSize 50 "
            + "--populationSizeWithArchive 100 "
            + "--algorithmResult externalArchive  "
            + "--externalArchive hypervolumeArchive "
            + "--ranking dominanceRanking "
            + "--densityEstimator crowdingDistance "
            + "--offspringPopulationSize 50 "
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

    EvolutionaryAlgorithm<TM_MSASolution> algorithm = configurableAlgorithm.create();

    EvaluationObserver evaluationObserver = new EvaluationObserver(1000);
    algorithm.observable().register(evaluationObserver);

    RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
        new RunTimeChartObserver<>(
            "NSGA-II", 80, 1000,
            referenceFrontFileName, "F1", "F2");
    algorithm.observable().register(runTimeChartObserver);

    algorithm.run();

    JMetalLogger.logger.info("Total computing time: " + algorithm.totalComputingTime()); ;

    new SolutionListOutput(algorithm.result())
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();
  }
}
