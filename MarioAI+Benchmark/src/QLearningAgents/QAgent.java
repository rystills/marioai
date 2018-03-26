package QLearningAgents;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.LevelScene;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment; 
import ch.idsia.benchmark.mario.environments.MarioEnvironment;

public class QAgent extends BasicMarioAIAgent implements Agent {
	
	// path finding
    final double alpha = 0.1;
    final double gamma = 0.9;
    
    //states
    final int stateA = 0;
    final int stateB = 1;
    final int stateC = 2;
    final int stateD = 3;
    final int stateE = 4;
    final int stateF = 5;
    final int statesCount = 6;
    final int[] states = new int[]{stateA,stateB,stateC,stateD,stateE,stateF};
    String[] stateNames = new String[] { 
    		"Grounded", 				//A: on ground
    		"GroundedNearImpediment",	//B: on ground - next to wall, gap, or enemy
    		"AirRising", 				//C: in air - rising
    		"AirRisingNearEnemy", 		//D: in air - rising & near enemy
    		"AirFalling", 				//E: in air - falling
    		"AirFallingNearEnemy" 		//F: in air - falling & near enemy
    		};
 
    //state transitions
    int[] actionsFromA = new int[] { stateA, stateB, stateC, stateD };
    int[] actionsFromB = new int[] { stateA, stateB, stateC, stateD };
    int[] actionsFromC = new int[] { stateC, stateD, stateE, stateF };
    int[] actionsFromD = new int[] { stateC, stateD, stateE, stateF };
    int[] actionsFromE = new int[] { stateA, stateB, stateE, stateF};
    int[] actionsFromF = new int[] { stateA, stateB, stateC, stateD, stateE, stateF };
    int[][] actions = new int[][] { actionsFromA, actionsFromB, actionsFromC,
            actionsFromD, actionsFromE, actionsFromF };
	
    //reward & Q tables
	int[][] R = new int[statesCount][statesCount]; // reward lookup
	double[][] Q = new double[statesCount][statesCount]; // Q learning
            
	public QAgent() {
	    super("QAgent");
	    reset();
	}
	
	public void integrateObservation(Environment environment) {
		super.integrateObservation(environment);
	}
	
	public void reset() {
	    action = new boolean[Environment.numberOfButtons];
	    action[Mario.KEY_RIGHT] = true;
	    action[Mario.KEY_SPEED] = true;
	}
	
	
	public boolean[] getAction() {
	    //return final results
	    return action;
	}
}