package org.tm_msaligner.auto.parameter;

import java.util.List;
import org.tm_msaligner.crossover.SPXMSACrossover;
import org.tm_msaligner.solution.TM_MSASolution;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Factory for crossover operators.
 */
public class CrossoverMSAParameter extends CategoricalParameter {

  public CrossoverMSAParameter(List<String> crossoverOperators) {
    super("crossover", crossoverOperators);
  }

  public CrossoverOperator<TM_MSASolution> getParameter() {
    Double crossoverProbability = (Double) findGlobalParameter("crossoverProbability").value();

    CrossoverOperator<TM_MSASolution> result;
    switch (value()) {
      case "SPX":
        result = new SPXMSACrossover(crossoverProbability) ;
        break ;
      default:
        throw new JMetalException("Crossover operator does not exist: " + name());
    }

    return result;
  }
}
