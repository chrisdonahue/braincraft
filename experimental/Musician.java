import braincraft.*;

import java.util.ArrayList;
//import org.jfugue.Player;


public class Musician implements Thought
{
	public double evaluate(Brain b)
	{
		ArrayList<String> chordProgression = new ArrayList<String>();
		
		// Set up first two chords of sunken cathedrale
		String crd1 = "Cmaj5q";
		String crd2 = "Dmin5q";
		chordProgression.add(crd1);
		chordProgression.add(crd2);
		
		// Loop to create chords
		for (int i = 2; i < 10; i++)
		{
			double[] inputarr = new double[4];
			double[] output = new double[2];
			double[] chord1 = chordToValues(chordProgression.get(i-2));
			double[] chord2 = chordToValues(chordProgression.get(i-1));
			inputarr[0] = chord1[0] / 12;
			inputarr[1] = chord1[1];
			inputarr[2] = chord2[0] / 12;
			inputarr[3] = chord2[1];
			//System.out.println("INPUT: " + inputarr[0] + ", " + inputarr[1] + ", " + inputarr[2] + ", " + inputarr[3]);
			try
			{
				for (int j = 0; j < 2; j++)
				{
					output = b.pumpNet(inputarr);
					//System.out.println("OUTPUT: " + output[0] + ", " + output[1]);
				}
			}
			catch (NetworkInputException e)
			{
				System.out.println("oops");
			}
			chordProgression.add(valuesToChord(output));
		}
		
		// Calculate produced string
		String chordProgString = "";
		for (String s : chordProgression)
		{
			chordProgString += s + " ";
		}
		chordProgString.substring(0, chordProgString.length() - 1);
		
		// Compare to cathedrale
		String cathedraleString = "Cmaj5q Dmin5q Gmaj5q Fmaj5q Emin5q Dmin5q Dmin5q Amin5q Emin5q Fmaj5q";
		int i = 0;
		int fitness = 0;
		for (String s : cathedraleString.split(" "))
		{
			if (s.equals(chordProgression.get(i)))
				fitness++;
			i++;
		}
		System.out.println(chordProgString);

		return fitness;
	}

	protected String valuesToChord(double[] vals)
	{
		int note = (int) (vals[0] * 12);
		String noteString;
		
		switch (note)
		{
		case 0:
			noteString = "C";
			break;
		case 1:
			noteString = "C#";
			break;
		case 2:
			noteString = "D";
			break;
		case 3:
			noteString = "D#";
			break;
		case 4:
			noteString = "E";
			break;
		case 5:
			noteString = "F";
			break;
		case 6:
			noteString = "F#";
			break;
		case 7:
			noteString = "G";
			break;
		case 8:
			noteString = "G#";
			break;
		case 9:
			noteString = "A";
			break;
		case 10:
			noteString = "A#";
			break;
		case 11:
			noteString = "B";
			break;
		default:
			noteString = "NULL";
			break;
		}

		if (vals[1] < 0.5)
			noteString += "maj";
		else
			noteString += "min";
		
		noteString += "5q";
		return noteString;
	}

	protected double[] chordToValues(String chord)
	{
		double[] retarr = new double[2];
		String note = chord.substring(0, 1);
		if (note.equals("C"))
			retarr[0] = 0;
		else if (note.equals("C#"))
			retarr[0] = 1;
		else if (note.equals("D"))
			retarr[0] = 2;
		else if (note.equals("D#"))
			retarr[0] = 3;
		else if (note.equals("E"))
			retarr[0] = 4;
		else if (note.equals("F"))
			retarr[0] = 5;
		else if (note.equals("F#"))
			retarr[0] = 6;
		else if (note.equals("G"))
			retarr[0] = 7;
		else if (note.equals("G#"))
			retarr[0] = 8;
		else if (note.equals("A"))
			retarr[0] = 9;
		else if (note.equals("A#"))
			retarr[0] = 10;
		else if (note.equals("B"))
			retarr[0] = 11;
		if (chord.contains("maj"))
			retarr[1] = 0;
		else
			retarr[1] = 1;
		return retarr;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		new Musician();
	}

	public Musician()
	{
		
		Brain c = Brain.loadText("text.txt", -2);
		//evaluate(c);
		
		Population six = new TribePopulation(this, 10, c);
		six.sigmoidCoefficient = -2.0;
		Braincraft.gatherStats = false;
		six.thinkUntil(7.0);
		six.getChamp().saveText("analogue.txt");
		
		
		/*
		Braincraft.gatherStats = false;
		Population band = new Population(this, 100, 4, 2);
		band.nodeMutationRate = 0.05;
		band.linkMutationRate = 0.1;
		band.sigmoidCoefficient = -2.0;
		band.thinkUntil(6.0);
		band.getChamp().saveText("text.txt");
		//Braincraft.writeStats("stats.txt");
		//Braincraft.writeLog("log.txt");
		*/
		
	}
}
