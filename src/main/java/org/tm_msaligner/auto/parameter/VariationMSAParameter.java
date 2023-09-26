package org.tm_msaligner.auto.parameter;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class VariationMSAParameter extends CategoricalParameter {
  public VariationMSAParameter(List<String> variationStrategies) {
    super("variation", variationStrategies);
  }

  public Variation getParameter() {
    Variation result;
    int offspringPopulationSize = (Integer)findGlobalParameter("offspringPopulationSize").value() ;

    if ("crossoverAndMutationVariation".equals(value())) {
      CrossoverMSAParameter crossoverParameter =
          (CrossoverMSAParameter) findSpecificParameter("crossover");
      MutationMSAParameter mutationParameter = (MutationMSAParameter) findSpecificParameter("mutation");

      result = new CrossoverAndMutationVariation<>(
              offspringPopulationSize, crossoverParameter.getParameter(),
              mutationParameter.getParameter());
    } else {
      throw new JMetalException("Variation component unknown: " + value());
    }

    return result;
  }

  @Override
  public String name() {
    return "variation";
  }
}

