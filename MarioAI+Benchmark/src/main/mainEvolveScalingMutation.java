package main;

import ch.idsia.agents.learning.SmarterMLPAgent;
import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.tasks.ProgressTask;
import ch.idsia.evolution.Evolvable;
import ch.idsia.evolution.ea.SmarterES;
import ch.idsia.scenarios.oldscenarios.Stats;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.utils.wox.serial.Easy;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: Jun 14, 2009
 * Time: 2:15:51 PM
 */
public class mainEvolveScalingMutation
{
    final static int generations = 100;
    final static int populationSize = 25;

    public static void main(String[] args)
    {
        CmdLineOptions options = new CmdLineOptions(new String[0]);
        options.setPauseWorld(false);
        Evolvable initial = new SmarterMLPAgent();
        options.setFPS(GlobalOptions.MaxFPS);
        options.setLevelDifficulty(0);
        options.setVisualization(false);
        ProgressTask task = new ProgressTask(options); //defines fitness function
        options.setLevelRandSeed((int) (Math.random() * Integer.MAX_VALUE));
        SmarterES es = new SmarterES(task, initial, populationSize, populationSize/2);
        System.out.println("Evolving " + initial + " with task " + task);   
        final String fileName = "evolved" + (int) (Math.random() * Integer.MAX_VALUE) + ".xml";
        
        float mutationMagnitude = .3f;
        for (int gen = 0; gen < generations; gen++)
        {
        	if (gen == generations-1) {
        		options.setVisualization(true);
        	}
            es.nextGeneration(mutationMagnitude);
            mutationMagnitude -= .003f;
            double bestResult = es.getBestFitnesses()[0];
            System.out.println("Generation " + gen + " best " + bestResult);
            Easy.save(es.getBests()[0], fileName);
        }
        Stats.main(new String[]{fileName, "1"});
    }
}
