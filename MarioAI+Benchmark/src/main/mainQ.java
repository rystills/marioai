package main;

import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.benchmark.tasks.MarioCustomSystemOfValues;
import ch.idsia.tools.CmdLineOptions;

public final class mainQ {
	public static void main(String[] args) {
		//set agent and level options
		final String argsString = "-vis on -ld 0 -ls 0 -ag QLearningAgents.QAgent";
		final CmdLineOptions cmdLineOptions = new CmdLineOptions(argsString);
	    final BasicTask basicTask = new BasicTask(cmdLineOptions);
	
	    //run sim and get evaluation score
	    basicTask.reset(cmdLineOptions);
	    basicTask.runOneEpisode();
	    System.out.println(basicTask.getEnvironment().getEvaluationInfoAsString());
	    
	    //display weighted score and exit
	    final MarioCustomSystemOfValues sov = new MarioCustomSystemOfValues();
	    System.out.println(basicTask.getEnvironment().getEvaluationInfo().computeWeightedFitness(sov));
	    System.exit(0);
	}
}