package org.uma.khaos.tm_msaligner.auto.irace;

import static org.uma.jmetal.util.SolutionListUtils.getMatrixWithObjectiveValues;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.khaos.tm_msaligner.auto.algorithm.ConfigurableTMMAligner;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;

public class iraceRunner {
  public static void main(String[] args) throws IOException {
    JMetalLogger.configureLoggers(new File("logging.properties"));
    //JMetalLogger.logger.setLevel(Level.OFF);

    ConfigurableTMMAligner tmmAligner = new ConfigurableTMMAligner();
    tmmAligner.parse(args);

    EvolutionaryAlgorithm<TM_MSASolution> algorithm = tmmAligner.create();
    algorithm.run();

    String referenceFrontFile =
        "data/referenceFronts/" + tmmAligner.referenceFrontFilename.value();

    double[][] referenceFront = VectorUtils.readVectors(referenceFrontFile, ",");
    double[][] front = getMatrixWithObjectiveValues(algorithm.result()) ;

    double[][] normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);
    double[][] normalizedFront =
            NormalizeUtils.normalize(
                    front,
                    NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
                    NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    var qualityIndicator = new PISAHypervolume(normalizedReferenceFront) ;
    System.out.println(qualityIndicator.compute(normalizedFront) * -1.0) ;
  }
}
