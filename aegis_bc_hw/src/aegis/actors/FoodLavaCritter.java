package aegis.actors;

import aegis.core.Actor;
import aegis.core.Stage;
import braincraft.Brain;

public class FoodLavaCritter extends FoodCritter
{
	public FoodLavaCritter(Brain b, Stage s)
	{
		super(b, s);
	}
	
	/**
	 * Gets data from the stage and converts it into inputs for the brain.
	 * @return inputs for the brain
	 */
	@Override
	protected double[] getBrainInputs()
	{
		double in[] = new double[6];
		
		//your code here
		//should be similar to code in FoodCritter.getBrainInputs()
		//you dont have to use 6 inputs, but if you don't, modify the instantiation of 
		//	TribePopulation in FoodLavaExperiment's constructor
		
		return in;
	}
	
	/**
	 * Takes the outputs of the brain and applies them to the simulation world.
	 * @param out 
	 */
	@Override
	protected void useBrainOutputs(double[] out)
	{
		//your code here
		//should be similar to code in FoodCritter.getBrainInputs()
		//in my implementation, this was the same as in FoodCritter
	}
	
	@Override
	protected void moveForward()
	{
		//your code here
		//should be similar to code in FoodCritter.getBrainInputs()
		//add support for score reduction when lava is encountered
	}
}
