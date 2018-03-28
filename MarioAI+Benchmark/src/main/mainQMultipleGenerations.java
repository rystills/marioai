package main;

import ch.idsia.agents.Agent;
import QLearningAgents.QAgent;
import QLearningAgents.Trainer;
import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.benchmark.tasks.MarioCustomSystemOfValues;
import ch.idsia.benchmark.tasks.ProgressTask;
import ch.idsia.evolution.Evolvable;
import ch.idsia.evolution.ea.SmarterES;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.utils.wox.serial.Easy;

public final class mainQMultipleGenerations {
    final static int generations = 100;
    
	public static void main(String[] args) {		
		//prepare options for training
		CmdLineOptions options = new CmdLineOptions(new String[0]);
        options.setPauseWorld(false);
        options.setFPS(GlobalOptions.MaxFPS);
        options.setLevelDifficulty(0); //change level difficulty
        options.setLevelRandSeed(0); //change level seed
        options.setVisualization(false);
        
        Agent a = new QAgent();
        ProgressTask task = new ProgressTask(options); //defines fitness function
        Trainer t = new Trainer(task,a);
        
        for (int gen = 0; gen < generations; gen++) {
            System.out.println("results of simulation " + gen + ": " + t.simulate());
        }
        //run the final version of our evolved agent
		System.out.println("Showing results of training after " + generations + " generations...");
		options.setFPS(24);
		options.setVisualization(true);
		System.out.println("results of final simulation: " + t.simulate());
	}
}