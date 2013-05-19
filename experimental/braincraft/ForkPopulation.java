package braincraft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * @author Chris Donahue
 * 
 *         This is an experimental new genetic algorithm I am playing with. It
 *         is not close to done yet.
 * 
 */
public class ForkPopulation extends Population {
	public double survivalRatePerGeneration;

	private Set<Double> forkValues;
	private LinkedList<Double> sortedForks;
	private ArrayList<DNA> thisGeneration;

	// CONSTRUCTORS:
	/**
	 * Constructor for a new Population object.
	 * 
	 * @param numInputs
	 *            the number of inputs for a population
	 * @param numOutputs
	 *            the number of outputs for a population
	 * @param popSize
	 *            the size of the population
	 * @param inter
	 *            the Thought experiment for this Population
	 */
	public ForkPopulation(int popSize, int in, int out) {
		super(popSize, in, out);
	}

	/**
	 * Constructor for a new Population object with default population size.
	 * 
	 * @param numInputs
	 *            the number of inputs for a population
	 * @param numOutputs
	 *            the number of outputs for a population
	 */
	public ForkPopulation(int numInputs, int numOutputs) {
		this(Braincraft.populationSize, numInputs, numOutputs);
	}

	public ForkPopulation(int popSize, Brain b) {
		super(popSize, b);
	}

	protected void registerDNA(DNA d) {
		super.registerDNA(d);
		thisGeneration.add(d);
	}

	public void thinkFor(int numGens) {

	}

	public void thinkUntil(double threshold) {

	}

	public void addFork(Double d) {
		forkValues.add(d);
	}

	protected void setForks(LinkedList<Double> forks) {
		sortedForks = forks;
	}

	protected void repopulate() {
		super.repopulate();

		// Set up sorted fork list
		if (sortedForks == null) {
			LinkedList<Double> sorted = new LinkedList<Double>();
			sorted.addAll(forkValues);
			Collections.sort(sorted);
			sortedForks = sorted;
		}

		if (champ.dna.fitness >= sortedForks.peekFirst()) {
			double highestThresholdPassed;
			do {
				highestThresholdPassed = sortedForks.pollFirst();
			} while (champ.dna.fitness >= highestThresholdPassed);

			String s = "POPULATION " + ID + " has reached specified threshold "
					+ highestThresholdPassed + ". Brain " + champ.ID
					+ " surpassed this threshold.";
			killPopulation(s);

			ForkPopulation newpop = new ForkPopulation(populationSize, champ);
			newpop.setForks(sortedForks);
			newpop.copyParametersFrom(this);
		} else {
			Collections.sort(thisGeneration);

		}
	}

	protected void initialSetup() {
		super.initialSetup();
		thisGeneration = new ArrayList<DNA>();
		forkValues = new HashSet<Double>();
		survivalRatePerGeneration = Braincraft.survivalRatePerGeneration;
	}

	@Override
	public Collection<Brain> getBrains(int num) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void sanityCheck() {
		// TODO Auto-generated method stub

	}

	@Override
	public Brain getBrain() {
		// TODO Auto-generated method stub
		return null;
	}
}