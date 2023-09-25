package org.uma.khaos.tm_msaligner.auto.irace;

import java.util.logging.Level;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoNSGAII;
import org.uma.jmetal.auto.irace.parameterfilegeneration.IraceParameterFileGenerator;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.khaos.tm_msaligner.auto.algorithm.ConfigurableTMMAligner;

/**
 * Program to generate the irace configuration file for class {@link AutoNSGAII}
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 */
public class ConfigurableTMMAlignerParameterFileGenerator {
  public static void main(String[] args) {
    IraceParameterFileGenerator parameterFileGenerator = new IraceParameterFileGenerator() ;
    parameterFileGenerator.generateConfigurationFile(new ConfigurableTMMAligner()) ;
  }
}
