package org.uma.khaos.tm_msaligner.problem;


import org.uma.jmetal.problem.Problem;

public abstract class AbstractGenericTM_MSAProblem<S> implements Problem<S> {

  private int numberOfVariables = 0;
  private int numberOfObjectives = 0;
  private String name = "TM-MSA Problem";

  public AbstractGenericTM_MSAProblem() {  }

  protected void setName(String name) {    this.name = name;  }
  @Override
  public String name() {    return name;    }

  protected void setNumberOfVariables(int numberOfVariables) { this.numberOfVariables = numberOfVariables;  }
  @Override
  public int numberOfVariables() {    return this.numberOfVariables;  }

  protected void setNumberOfObjectives(int numberOfObjectives) {this.numberOfObjectives = numberOfObjectives; }

  @Override
  public int numberOfObjectives() {     return this.numberOfObjectives;       }
  @Override
  public int numberOfConstraints() {    return 0;  }


}
