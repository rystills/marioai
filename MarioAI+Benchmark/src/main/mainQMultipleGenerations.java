package main;

import ch.idsia.agents.Agent;
import QLearningAgents.QAgent;
import QLearningAgents.Trainer;
import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.tasks.ProgressTask;
import ch.idsia.tools.CmdLineOptions;

public final class mainQMultipleGenerations {
    final static int generations = 10;
    
	public static void main(String[] args) {		
		//prepare options for training
		CmdLineOptions options = new CmdLineOptions(new String[0]);
        options.setPauseWorld(false);
        options.setFPS(GlobalOptions.MaxFPS);
        options.setLevelDifficulty(25); //change level difficulty
        options.setLevelRandSeed(2); //change level seed
        options.setVisualization(false);
        
        //init agent and training environment
        Agent a = new QAgent(false);
        ProgressTask task = new ProgressTask(options); //defines fitness function
        Trainer t = new Trainer(task,a);
        
        //train
        for (int gen = 0; gen < generations; gen++) {
            System.out.print("results of simulation " + gen + ": ");
            ((QAgent)a).printResult();
        }
        
        //run the final version of our trained agent
		System.out.println("Showing results of training after " + generations + " generations...");
		options.setFPS(24);
		options.setVisualization(true);
		System.out.println("results of final simulation: " + t.simulate());
	}
}