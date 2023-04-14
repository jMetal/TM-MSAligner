package org.uma.khaos.tm_msaligner.solutionscreation;

import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.khaos.tm_msaligner.crossover.SPXMSACrossover;
import org.uma.khaos.tm_msaligner.problem.StandardTMMSAProblem;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;
import org.uma.khaos.tm_msaligner.util.AAArray;

import java.util.ArrayList;
import java.util.List;

public class PreComputedMSAsSolutionsCreation implements SolutionsCreation<TM_MSASolution> {

    private final int numberOfSolutionsToCreate;
    private final StandardTMMSAProblem problem;

    public PreComputedMSAsSolutionsCreation(StandardTMMSAProblem problem, int numberOfSolutionsToCreate) {
        this.numberOfSolutionsToCreate = numberOfSolutionsToCreate;
        this.problem = problem;
    }

    @Override
    public List<TM_MSASolution> create() {
        List<TM_MSASolution> population = new ArrayList<>(numberOfSolutionsToCreate);
        JMetalRandom randomGenerator = JMetalRandom.getInstance();

        for (List<AAArray> sequenceList : problem.listOfPrecomputedStringAlignments) {
            TM_MSASolution newIndividual = new TM_MSASolution(sequenceList, problem);
            population.add(newIndividual);
        }

        int parent1, parent2;
        List<TM_MSASolution> children, parents;
        SPXMSACrossover crossover = new SPXMSACrossover(1);

        while (population.size() < numberOfSolutionsToCreate) {
            parents = new ArrayList<TM_MSASolution>();

            parent1 = randomGenerator.nextInt(0, population.size() - 1);
            do {
                parent2 = randomGenerator.nextInt(0, population.size() - 1);
            } while (parent1 == parent2);
            parents.add(population.get(parent1));
            parents.add(population.get(parent2));

            children = crossover.execute(parents);

            population.add(children.get(0));
            population.add(children.get(1));
        }
        return population;

    }
}
