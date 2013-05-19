import braincraft.*;

/**
 * @author Chris Donahue
 * 
 *         AND is a verbose learning experiment example utilizing most of
 *         Braincraft's features. It evolves networks that approximate a 2-bit
 *         "AND" function.
 */
public class AND {

	public static void main(String[] args) {
		new AND();
	}

	/**
	 * Constructs a new AND() experiment.
	 */
	public AND() {
		// Toggles whether or not the log prints to console. False by default.
		Braincraft.logToSystemOut = true;

		// Toggles statistics gathering for the visualizer. If set to true,
		// memory usage will increase drastically as the library is saving every
		// Brain produced. False by default.
		Braincraft.gatherStats = false;

		// Creates a new Population with NEAT-style population control.
		// Population is size 30 and the networks have 2 inputs and 1 output.
		TribePopulation pop = new TribePopulation(20, 2, 1);

		// All parameters for the population with default values overridden. See
		// README for a description of each parameter.
		pop.sigmoidCoefficient = -4.9;
		pop.perWeightMutationRate = 0.9;
		pop.weightMutationRate = 0.8;
		pop.linkMutationRate = 0.1;
		pop.linkDisableRate = 0;
		pop.nodeMutationRate = 0.05;
		pop.disabledRate = 0.75;
		pop.inheritFromHigherFitRate = 0.8;

		// NEAT-specific parameters
		pop.c1 = 1.0;
		pop.c2 = 1.0;
		pop.c3 = 0.4;
		pop.tribeCompatibilityThreshold = 3.0;
		pop.percentageOfTribeToKillBeforeReproduction = 0.5;

		// Loop to acquire and test Brains.
		double fitness = 0;
		int counter = 0;

		while (fitness < 4 && counter < 20000) {
			// Gets a single Brain from the library and evaluates it.
			// getBrains(int num) will return a Collection of Brains.
			Brain b = pop.getBrain();
			fitness = evaluateBrain(b);
			// Reports the fitness of the Brain to the library. An entire
			// generation must have its fitness reported before reproduction can
			// take place.
			pop.reportFitness(b, fitness);

			// Save a copy of the champion Brain for later usage.
			if (fitness == 4) {
				// Saves a Java serialized object of the Brain.
				b.saveObject("andnetwork.obj");
				// Saves a text representation of the Brain.
				b.saveText("andnetwork.txt");
				// Kills the Population cleanly so it can no longer produce
				// networks.
				pop.killPopulation("AND network found and saved.");
			}
			counter++;
		}

		// Writes the statistics file for use by the visualizer
		Braincraft.writeStats("stats.txt");
		// Writes the evolution log file
		Braincraft.writeLog("log.txt");
	}

	/**
	 * Evaluates a neural network for its use as an AND gate. Output values less
	 * than 0.5 interpreted as 0, otherwise 1.
	 * 
	 * @param b
	 *            Brain to evaluate
	 * @return a fitness 0-4 based on how many correct answers the 2-bit AND
	 *         network produced.
	 */
	public double evaluateBrain(Brain b) {
		// Set of possible values for first bit
		boolean[] xarr = { false, true };
		// Set of possible values for second bit
		boolean[] yarr = { false, true };

		int fitness = 0;

		// Loop to test network against 2-bit XOR
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				// Create the input array
				double[] arr = new double[2];
				arr[0] = x;
				arr[1] = y;

				double[] output = null;
				for (int i = 0; i < 4; i++) {
					// pumpNet pumps the neural network a single time. For more
					// complicated networks, more pumps are required to trickle
					// the values to the output nodes.
					output = b.pumpNet(arr);
				}

				// Test network result against actual result and reward fitness
				// accordingly. It is much harder to evolve a network that
				// produces discrete 0 and 1 values so any value less than 0.5
				// is considered a 0.
				boolean result = true;
				if (output[0] < 0.5) {
					result = false;
				}
				if (result == (xarr[x] && yarr[y])) {
					fitness++;
				}

				// This clears activity information from the network so the next
				// trial will start with the fresh network. Clearing activity
				// between trials essentially removes memory features of the
				// network, appropriate for logic gates.
				b.clearActivity();
			}
		}
		return fitness;
	}
}