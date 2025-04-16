package org.tm_msaligner;

import java.io.IOException;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.tm_msaligner.solution.TM_MSASolution;

public class ConfigurableTM_MSAligner {

  public static void main(String[] args) throws IOException {
    var configurableAlgorithm = new org.tm_msaligner.auto.algorithm.ConfigurableTM_MSAligner();
    configurableAlgorithm.parse(args);

    org.tm_msaligner.auto.algorithm.ConfigurableTM_MSAligner.print(configurableAlgorithm.fixedParameterList());
    org.tm_msaligner.auto.algorithm.ConfigurableTM_MSAligner.print(configurableAlgorithm.configurableParameterList());

    EvolutionaryAlgorithm<TM_MSASolution> algorithm = configurableAlgorithm.create();

    algorithm.run();

    JMetalLogger.logger.info("Total computing time: " + algorithm.totalComputingTime()); ;

    new SolutionListOutput(algorithm.result())
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();
  }
}
