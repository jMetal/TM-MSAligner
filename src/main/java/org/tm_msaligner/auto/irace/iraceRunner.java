package org.tm_msaligner.auto.irace;

import static org.uma.jmetal.util.SolutionListUtils.getMatrixWithObjectiveValues;

import java.io.IOException;
import java.util.logging.Level;
import org.tm_msaligner.solution.TM_MSASolution;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.VectorUtils;
import org.tm_msaligner.auto.algorithm.ConfigurableTM_MSAligner;

public class iraceRunner {
  public static void main(String[] args) throws IOException {
    JMetalLogger.logger.setLevel(Level.OFF);

    ConfigurableTM_MSAligner tmmAligner = new ConfigurableTM_MSAligner();
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
