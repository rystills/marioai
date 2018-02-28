package main;

import ch.idsia.agents.Agent;
import ch.idsia.agents.learning.SmarterMLPAgent;
import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.tasks.ProgressTask;
import ch.idsia.evolution.Evolvable;
import ch.idsia.evolution.ea.SmarterES;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.utils.wox.serial.Easy;

public class mainEvolveScalingMutation {
    final static int generations = 3;
    final static int populationSize = 50;

    public static void main(String[] args) {
        CmdLineOptions options = new CmdLineOptions(new String[0]);
        options.setPauseWorld(false);
        Evolvable initial = new SmarterMLPAgent();
        options.setFPS(GlobalOptions.MaxFPS);
        options.setLevelDifficulty(25);
        options.setLevelRandSeed(2);
        options.setVisualization(false);
        ProgressTask task = new ProgressTask(options); //defines fitness function
        SmarterES es = new SmarterES(task, initial, populationSize, populationSize/2);
        System.out.println("Evolving " + initial + " with task " + task);   
        final String fileName = "evolved" + (int) (Math.random() * Integer.MAX_VALUE) + ".xml";
        
        float mutationMagnitude = .3f;
        for (int gen = 0; gen < generations; gen++) {
            es.nextGeneration(mutationMagnitude);
            mutationMagnitude -= .003f;
            double bestResult = es.getBestFitnesses()[0];
            System.out.println("Generation " + gen + " best " + bestResult);
            Easy.save(es.getBests()[0], fileName);
        }
        //run the final version of our evolved agent
		System.out.println("Showing results of final evolved agent with score: " + es.getBestFitnesses()[0]);
		options.setFPS(24);
		options.setVisualization(true);
        es.task.evaluate((Agent)es.getBests()[0]);
    }
}
