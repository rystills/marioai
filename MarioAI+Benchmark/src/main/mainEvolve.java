package main;

import ch.idsia.agents.Agent;
import ch.idsia.agents.learning.SmarterMLPAgent;
import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.tasks.ProgressTask;
import ch.idsia.evolution.Evolvable;
import ch.idsia.evolution.ea.SmarterES;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.utils.wox.serial.Easy;

public class mainEvolve {
    final static int generations = 100;
    final static int populationSize = 25;

    public static void main(String[] args) {
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
        for (int gen = 0; gen < generations; gen++) {
            es.nextGeneration();
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
