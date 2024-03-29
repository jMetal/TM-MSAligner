package org.tm_msaligner.auto.irace;

import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoNSGAII;
import org.uma.jmetal.auto.irace.parameterfilegeneration.IraceParameterFileGenerator;
import org.tm_msaligner.auto.algorithm.ConfigurableTM_MSAligner;

/**
 * Program to generate the irace configuration file for class {@link AutoNSGAII}
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 */
public class ConfigurableTMMAlignerParameterFileGenerator {
  public static void main(String[] args) {
    IraceParameterFileGenerator parameterFileGenerator = new IraceParameterFileGenerator() ;
    parameterFileGenerator.generateConfigurationFile(new ConfigurableTM_MSAligner()) ;
  }
}
