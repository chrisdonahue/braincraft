package aegis.experiments;

import java.util.Random;

import aegis.actors.Food;
import aegis.actors.FoodLavaCritter;
import aegis.actors.Lava;
import braincraft.Brain;
import braincraft.TribePopulation;

/**
 * Simple experiment to see if hungry critters can evolve to get better at eating.
 * @author Prad
 *
 */
public class FoodLavaExperiment extends FoodExperiment
{	
	/**
	 * Takes in a bunch of parameters, sets up the BrainCraft population
	 */
	public FoodLavaExperiment(String[] args)
	{
		super(args);
		
		//only difference from FoodExperiment is number of inputs: 6
		pop = new TribePopulation(popSize, 6, 3);
		pop.nodeMutationRate = nodeMutationRate;
		pop.linkMutationRate = linkMutationRate;
		pop.weightMutationRate = weightMutationRate;
		pop.linkDisableRate = linkDisableRate;
		
	}
	
	/**
	 * Simulates one critter. If the critter's fitness passes a threshold, it's actions are replayed. 
	 * 
	 * @param b
	 * @param r
	 * @return
	 */
	@Override
	protected int runSimulation(Brain b, Random r)
	{
		//your code here
		//copy the FoodExperiment code and modify it to support lava
	}

}
