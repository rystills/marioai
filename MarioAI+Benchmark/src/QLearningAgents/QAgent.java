package QLearningAgents;

import java.util.Random;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.LevelScene;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment; 
import ch.idsia.benchmark.mario.environments.MarioEnvironment;

public class QAgent extends BasicMarioAIAgent implements Agent {
	
	//Q constants
	final float epsilon = .05f;
	
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
            
    //input-action correspondance (not sure on this one)
    boolean actionInputPairs[][][] = new boolean[statesCount][6][Environment.numberOfButtons];
	
    //reward & Q tables
	int[][] R = new int[statesCount][statesCount]; // reward lookup
	double[][] Q = new double[statesCount][statesCount]; // Q values
            
	public QAgent() {
	    super("QAgent");
	    reset();
	    initRewards();
	    initInputPairs();
	}
	
	public void initInputPairs() {
		//inputs from A
		actionInputPairs[stateA][stateA][Mario.KEY_SPEED] = true;
		actionInputPairs[stateA][stateA][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateA][stateB][Mario.KEY_SPEED] = true;
		actionInputPairs[stateA][stateB][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateA][stateC][Mario.KEY_SPEED] = true;
		actionInputPairs[stateA][stateC][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateA][stateC][Mario.KEY_JUMP] = true;
		actionInputPairs[stateA][stateD][Mario.KEY_SPEED] = true;
		actionInputPairs[stateA][stateD][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateA][stateD][Mario.KEY_JUMP] = true;
		
		//inputs from B
		actionInputPairs[stateB][stateA][Mario.KEY_SPEED] = true;
		actionInputPairs[stateB][stateA][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateB][stateB][Mario.KEY_SPEED] = true;
		actionInputPairs[stateB][stateB][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateB][stateC][Mario.KEY_SPEED] = true;
		actionInputPairs[stateB][stateC][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateB][stateC][Mario.KEY_JUMP] = true;
		actionInputPairs[stateB][stateD][Mario.KEY_SPEED] = true;
		actionInputPairs[stateB][stateD][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateB][stateD][Mario.KEY_JUMP] = true;
		
		//inputs from C
		actionInputPairs[stateC][stateC][Mario.KEY_SPEED] = true;
		actionInputPairs[stateC][stateC][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateC][stateC][Mario.KEY_JUMP] = true;
		actionInputPairs[stateC][stateD][Mario.KEY_SPEED] = true;
		actionInputPairs[stateC][stateD][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateC][stateD][Mario.KEY_JUMP] = true;
		actionInputPairs[stateC][stateE][Mario.KEY_SPEED] = true;
		actionInputPairs[stateC][stateE][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateC][stateF][Mario.KEY_SPEED] = true;
		actionInputPairs[stateC][stateF][Mario.KEY_RIGHT] = true;
		
		//inputs from D
		actionInputPairs[stateD][stateC][Mario.KEY_SPEED] = true;
		actionInputPairs[stateD][stateC][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateD][stateC][Mario.KEY_JUMP] = true;
		actionInputPairs[stateD][stateD][Mario.KEY_SPEED] = true;
		actionInputPairs[stateD][stateD][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateD][stateD][Mario.KEY_JUMP] = true;
		actionInputPairs[stateD][stateE][Mario.KEY_SPEED] = true;
		actionInputPairs[stateD][stateE][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateD][stateF][Mario.KEY_SPEED] = true;
		actionInputPairs[stateD][stateF][Mario.KEY_RIGHT] = true;
		
		//inputs from E
		actionInputPairs[stateE][stateA][Mario.KEY_SPEED] = true;
		actionInputPairs[stateE][stateA][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateE][stateB][Mario.KEY_SPEED] = true;
		actionInputPairs[stateE][stateB][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateE][stateE][Mario.KEY_SPEED] = true;
		actionInputPairs[stateE][stateE][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateE][stateF][Mario.KEY_SPEED] = true;
		actionInputPairs[stateE][stateF][Mario.KEY_RIGHT] = true;
		
		//inputs from F
		actionInputPairs[stateF][stateA][Mario.KEY_SPEED] = true;
		actionInputPairs[stateF][stateA][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateF][stateB][Mario.KEY_SPEED] = true;
		actionInputPairs[stateF][stateB][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateF][stateC][Mario.KEY_SPEED] = true;
		actionInputPairs[stateF][stateC][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateF][stateC][Mario.KEY_JUMP] = true;
		actionInputPairs[stateF][stateD][Mario.KEY_SPEED] = true;
		actionInputPairs[stateF][stateD][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateF][stateD][Mario.KEY_JUMP] = true;
		actionInputPairs[stateF][stateE][Mario.KEY_SPEED] = true;
		actionInputPairs[stateF][stateE][Mario.KEY_RIGHT] = true;
		actionInputPairs[stateF][stateF][Mario.KEY_SPEED] = true;
		actionInputPairs[stateF][stateF][Mario.KEY_RIGHT] = true;
		
	}
	
	/**
	 * set appropriate rewards for transitioning from each state to each other possible state
	 */
	public void initRewards() {
		//rewards from A
		
		
		//rewards from B
        R[stateB][stateA] = 32;
        R[stateB][stateC] = 16;
        R[stateB][stateD] = 8;
        
        //rewards from C
        
        //rewards from D
        R[stateD][stateC] = 32;
        R[stateD][stateE] = 16;
        R[stateD][stateF] = 8;
        
        //rewards from E
        
        //rewards from F
        R[stateF][stateA] = 32;
        R[stateF][stateB] = 16;
        R[stateF][stateC] = 8;
        R[stateF][stateD] = 4;
        R[stateF][stateE] = 2;
    }
	
	public void integrateObservation(Environment environment) {
		super.integrateObservation(environment);
	}
	
	public void reset() {
	    action = new boolean[Environment.numberOfButtons];
	    action[Mario.KEY_RIGHT] = true;
	    action[Mario.KEY_SPEED] = true;
	}
	
	void runQ() {
		/*
        1. Set parameter , and environment reward matrix R 
        2. Initialize matrix Q as zero matrix 
        3. For each episode: Select random initial state 
           Do while not reach goal state o 
               Select one among all possible actions for the current state o 
               Using this possible action, consider to go to the next state o 
               Get maximum Q value of this next state based on all possible actions o 
               Compute o Set the next state as the current state
        */

       // For each episode
       Random rand = new Random();
       for (int i = 0; i < 10000; i++) { // train episodes
           // Select random initial state
       	System.out.println();
       	System.out.print("iteration");
       	System.out.print(i);
       	System.out.println();
       	System.out.print("starting from ");
       	System.out.println();
       	
           int state = rand.nextInt(statesCount);
           System.out.print(state);
           System.out.println();
           while (state != stateC) // goal state
           {
           	
           	System.out.print(state);
           	
           	// Select one among all possible actions for the current state
               int[] actionsFromState = actions[state];
                
               float diceRoll = rand.nextFloat();
               int index;
               if (diceRoll <= epsilon) {
               	//choose index at random
               	index = rand.nextInt(actionsFromState.length);
               }
               else {
               	//choose index with largest value
               	index = 0;
               	for (int j = 1; j < actionsFromState.length; ++j) {
               		if (actionsFromState[j] > actionsFromState[index]) {
               			index = j;
               		}
               	}
               }
               
               int action = actionsFromState[index];

               // Action outcome is set to deterministic in this example
               // Transition probability is 1
               // what happens when the transition is probabilistic? 
               int nextState = action; // data structure

               // Using this possible action, consider to go to the next state
               double q = Q(state, action);
               double maxQ = maxQ(nextState);
               int r = R(state, action);

               double value = q + alpha * (r + gamma * maxQ - q);
               setQ(state, action, value);

               // Set the next state as the current state
               state = nextState;
           }
           System.out.print(state);
       }
    }
	
	double maxQ(int s) {
        int[] actionsFromState = actions[s];
        double maxValue = Double.MIN_VALUE;
        for (int i = 0; i < actionsFromState.length; i++) {
            int nextState = actionsFromState[i];
            double value = Q[s][nextState];
 
            if (value > maxValue)
                maxValue = value;
        }
        return maxValue;
    }
	
	// get policy from state
    int policy(int state) {
        int[] actionsFromState = actions[state];
        double maxValue = Double.MIN_VALUE;
        int policyGotoState = state; // default goto self if not found
        for (int i = 0; i < actionsFromState.length; i++) {
            int nextState = actionsFromState[i];
            double value = Q[state][nextState];
 
            if (value > maxValue) {
                maxValue = value;
                policyGotoState = nextState;
            }
        }
        return policyGotoState;
    }
 
    double Q(int s, int a) {
        return Q[s][a];
    }
 
    void setQ(int s, int a, double value) {
        Q[s][a] = value;
    }
 
    int R(int s, int a) {
        return R[s][a];
    }
	
	
	public boolean[] getAction() {
		runQ();
	    //return final results
	    return action;
	}
}