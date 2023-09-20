package org.uma.khaos.tm_msaligner.auto.algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoConfigurableAlgorithm;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.auto.parameter.IntegerParameter;
import org.uma.jmetal.auto.parameter.Parameter;
import org.uma.jmetal.auto.parameter.PositiveIntegerValue;
import org.uma.jmetal.auto.parameter.RealParameter;
import org.uma.jmetal.auto.parameter.StringParameter;
import org.uma.jmetal.auto.parameter.catalogue.CrossoverParameter;
import org.uma.jmetal.auto.parameter.catalogue.ExternalArchiveParameter;
import org.uma.jmetal.auto.parameter.catalogue.MutationParameter;
import org.uma.jmetal.auto.parameter.catalogue.ProbabilityParameter;
import org.uma.jmetal.auto.parameter.catalogue.SelectionParameter;
import org.uma.jmetal.auto.parameter.catalogue.VariationParameter;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluationWithArchive;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.util.RankingAndDensityEstimatorPreference;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.khaos.tm_msaligner.algorithm.multiobjective.TM_M2Align;
import org.uma.khaos.tm_msaligner.auto.parameter.CrossoverMSAParameter;
import org.uma.khaos.tm_msaligner.auto.parameter.MutationMSAParameter;
import org.uma.khaos.tm_msaligner.auto.parameter.VariationMSAParameter;
import org.uma.khaos.tm_msaligner.problem.StandardTMMSAProblem;
import org.uma.khaos.tm_msaligner.problem.impl.MultiObjTMMSAProblem;
import org.uma.khaos.tm_msaligner.score.Score;
import org.uma.khaos.tm_msaligner.score.impl.AlignedSegment;
import org.uma.khaos.tm_msaligner.score.impl.SumOfPairsWithTopologyPredict;
import org.uma.khaos.tm_msaligner.solution.TM_MSASolution;
import org.uma.khaos.tm_msaligner.solutionscreation.PreComputedMSAsSolutionsCreation;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Blosum62;
import org.uma.khaos.tm_msaligner.util.substitutionmatrix.impl.Phat;

public class ConfigurableTMMAligner implements AutoConfigurableAlgorithm {

  public List<Parameter<?>> configurableParameterList = new ArrayList<>();
  public List<Parameter<?>> fixedParameterList = new ArrayList<>();
  private StringParameter problemNameParameter;
  private PositiveIntegerValue randomGeneratorSeedParameter;
  public StringParameter referenceFrontFilename;
  private PositiveIntegerValue maximumNumberOfEvaluationsParameter;
  private CategoricalParameter algorithmResultParameter;
  private ExternalArchiveParameter<TM_MSASolution> externalArchiveParameter;
  private PositiveIntegerValue populationSizeParameter;
  private IntegerParameter populationSizeWithArchiveParameter;
  private IntegerParameter offspringPopulationSizeParameter;
  private SelectionParameter<TM_MSASolution> selectionParameter;
  private VariationMSAParameter variationParameter;
  private MutationMSAParameter mutationParameter;
  private CrossoverMSAParameter crossoverParameter;
  private MultiObjTMMSAProblem problem ;

  public ConfigurableTMMAligner() {
    this.configure();
  }

  @Override
  public void parse(String[] arguments) {
    for (Parameter<?> parameter : fixedParameterList) {
      parameter.parse(arguments).check();
    }
    for (Parameter<?> parameter : configurableParameterList()) {
      parameter.parse(arguments).check();
    }
  }

  private void configure() {
    problemNameParameter = new StringParameter("problemName");
    populationSizeParameter = new PositiveIntegerValue("populationSize") ;
    referenceFrontFilename = new StringParameter("referenceFrontFileName");
    maximumNumberOfEvaluationsParameter =
        new PositiveIntegerValue("maximumNumberOfEvaluations");
    randomGeneratorSeedParameter = new PositiveIntegerValue("randomGeneratorSeed");

    fixedParameterList.add(populationSizeParameter);
    fixedParameterList.add(problemNameParameter);
    //fixedParameterList.add(referenceFrontFilename);
    fixedParameterList.add(maximumNumberOfEvaluationsParameter);
    fixedParameterList.add(randomGeneratorSeedParameter);

    algorithmResult();
    selection();
    variation();

    configurableParameterList.add(algorithmResultParameter);
    configurableParameterList.add(variationParameter);
    configurableParameterList.add(selectionParameter);
  }

  private void variation() {
    crossoverParameter = new CrossoverMSAParameter(List.of("SPX"));
    ProbabilityParameter crossoverProbability =
        new ProbabilityParameter("crossoverProbability");
    crossoverParameter.addGlobalParameter(crossoverProbability);

    mutationParameter =
        new MutationMSAParameter(List.of("insertRandomGap", "mergeAdjuntedGapsGroups", "shiftClosedGaps", "splitANonGapsGroup"));

    RealParameter mutationProbabilityFactor = new RealParameter("mutationProbabilityFactor",
        0.0, 2.0);
    mutationParameter.addGlobalParameter(mutationProbabilityFactor);

    offspringPopulationSizeParameter = new IntegerParameter("offspringPopulationSize", 1,
        400);

    variationParameter =
        new VariationMSAParameter(List.of("crossoverAndMutationVariation"));
    variationParameter.addGlobalParameter(offspringPopulationSizeParameter);
    variationParameter.addSpecificParameter("crossoverAndMutationVariation", crossoverParameter);
    variationParameter.addSpecificParameter("crossoverAndMutationVariation", mutationParameter);
  }

  private void algorithmResult() {
    algorithmResultParameter =
        new CategoricalParameter("algorithmResult", List.of("externalArchive", "population"));
    populationSizeWithArchiveParameter = new IntegerParameter("populationSizeWithArchive",10,
        200);
    externalArchiveParameter = new ExternalArchiveParameter<>(List.of("crowdingDistanceArchive"));
    algorithmResultParameter.addSpecificParameter(
        "externalArchive", populationSizeWithArchiveParameter);

    algorithmResultParameter.addSpecificParameter(
        "externalArchive", externalArchiveParameter);
  }

  private void selection() {
    selectionParameter = new SelectionParameter<>(Arrays.asList("tournament", "random"));
    IntegerParameter selectionTournamentSize =
        new IntegerParameter("selectionTournamentSize", 2, 10);
    selectionParameter.addSpecificParameter("tournament", selectionTournamentSize);
  }

  @Override
  public List<Parameter<?>> configurableParameterList() {
    return configurableParameterList;
  }

  @Override
  public List<Parameter<?>> fixedParameterList() {
    return fixedParameterList;
  }

  public EvolutionaryAlgorithm<TM_MSASolution> create() throws IOException {
    JMetalRandom.getInstance().setSeed(randomGeneratorSeedParameter.value());

    String refName = problemNameParameter.value();

    var weightGapOpenTM = 8;
    var weightGapExtendTM = 3;
    var weightGapOpenNonTM = 3;
    var weightGapExtendNonTM = 1;

    List<Score> scoreList = new ArrayList<>();
    scoreList.add(new SumOfPairsWithTopologyPredict(
        new Phat(8),
        new Blosum62(),
        weightGapOpenTM,
        weightGapExtendTM,
        weightGapOpenNonTM,
        weightGapExtendNonTM));
    scoreList.add(new AlignedSegment());

    String benchmarkPath = "data/benchmarks/ref7/" + refName + "/" ;
    String preComputedMSAPath = "data/precomputed_solutions/ref7/" +  refName + "/";
    String dataFile = benchmarkPath + refName + "_predicted_topologies.3line";

    List<String> preComputedFiles = new ArrayList<String>();
    preComputedFiles.add(preComputedMSAPath + refName + "kalign.fasta");
    preComputedFiles.add(preComputedMSAPath + refName + "mafft.fasta" );
    preComputedFiles.add(preComputedMSAPath + refName + "clustalw.fasta");
    preComputedFiles.add(preComputedMSAPath + refName + "muscle.fasta");
    preComputedFiles.add(preComputedMSAPath + refName + "t_coffee.fasta");
    preComputedFiles.add(preComputedMSAPath + refName + "tmt_coffee2023.fasta");
    //preComputedFiles.add(preComputedMSAPath + refName + "praline.fasta");

    problem = new MultiObjTMMSAProblem(dataFile, scoreList,
        preComputedFiles,refName);

    Archive<TM_MSASolution> archive = null;
    if (algorithmResultParameter.value().equals("externalArchive")) {
      externalArchiveParameter.setSize(populationSizeParameter.value());
      archive = externalArchiveParameter.getParameter();
      populationSizeParameter.value(populationSizeWithArchiveParameter.value());
    }

    Ranking<TM_MSASolution> ranking = new FastNonDominatedSortRanking<>(
        new DefaultDominanceComparator<>());
    DensityEstimator<TM_MSASolution> densityEstimator = new CrowdingDistanceDensityEstimator<>();
    MultiComparator<TM_MSASolution> rankingAndCrowdingComparator =
        new MultiComparator<>(
            Arrays.asList(
                Comparator.comparing(ranking::getRank),
                Comparator.comparing(densityEstimator::value).reversed()));

    var createInitialPopulation = new PreComputedMSAsSolutionsCreation(problem, populationSizeParameter.value());

    MutationMSAParameter mutationParameter = (MutationMSAParameter) variationParameter.findSpecificParameter(
        "mutation");
    mutationParameter.addNonConfigurableParameter("sequenceLength",
        problem.numberOfVariables());

    Variation<TM_MSASolution> variation = (Variation<TM_MSASolution>) variationParameter.getParameter();

    Selection<TM_MSASolution> selection =
        selectionParameter.getParameter(
            variation.getMatingPoolSize(), rankingAndCrowdingComparator);

    Evaluation<TM_MSASolution> evaluation;
    if (algorithmResultParameter.value().equals("externalArchive")) {
      evaluation = new SequentialEvaluationWithArchive<>(problem, archive);
    } else {
      evaluation = new SequentialEvaluation<>(problem);
    }

    RankingAndDensityEstimatorPreference<TM_MSASolution> preferenceForReplacement = new RankingAndDensityEstimatorPreference<>(
        ranking, densityEstimator);
    Replacement<TM_MSASolution> replacement =
        new RankingAndDensityEstimatorReplacement<>(preferenceForReplacement,
            Replacement.RemovalPolicy.ONE_SHOT);

    Termination termination =
        new TerminationByEvaluations(maximumNumberOfEvaluationsParameter.value());

    class EvolutionaryAlgorithmWithArchive extends EvolutionaryAlgorithm<TM_MSASolution> {

      private Archive<TM_MSASolution> archive;

      /**
       * Constructor
       *
       * @param name                      Algorithm name
       * @param initialPopulationCreation
       * @param evaluation
       * @param termination
       * @param selection
       * @param variation
       * @param replacement
       */
      public EvolutionaryAlgorithmWithArchive(String name,
          SolutionsCreation<TM_MSASolution> initialPopulationCreation,
          Evaluation<TM_MSASolution> evaluation, Termination termination,
          Selection<TM_MSASolution> selection, Variation<TM_MSASolution> variation,
          Replacement<TM_MSASolution> replacement,
          Archive<TM_MSASolution> archive) {
        super(name, initialPopulationCreation, evaluation, termination, selection, variation,
            replacement);
        this.archive = archive;
      }

      @Override
      public List<TM_MSASolution> result() {
        return archive.solutions() ;
      }
    }

    if (algorithmResultParameter.value().equals("externalArchive")) {
      return new EvolutionaryAlgorithmWithArchive(
          "NSGA-II",
          createInitialPopulation,
          evaluation,
          termination,
          selection,
          variation,
          replacement,
          archive);
    } else {
      return new EvolutionaryAlgorithm<>(
          "NSGA-II",
          createInitialPopulation,
          evaluation,
          termination,
          selection,
          variation,
          replacement);
    }
  }

  public static void print(List<Parameter<?>> parameterList) {
    parameterList.forEach(System.out::println);
  }
}
