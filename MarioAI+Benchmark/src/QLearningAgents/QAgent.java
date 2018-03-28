package QLearningAgents;

import java.text.DecimalFormat;
import java.util.Random;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.LevelScene;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment; 
import ch.idsia.benchmark.mario.environments.MarioEnvironment;

public class QAgent extends BasicMarioAIAgent implements Agent {
	final DecimalFormat df = new DecimalFormat("#.##");
	Random rand = new Random();
	
	//persistent variables for stateful logic
	int trueJumpCounter = 0;
	int trueSpeedCounter = 0;
	boolean wAppro = false;
	boolean gAppro = false;
	boolean eAppro = false;
	boolean movedDown = false;
	float prevX = 0;
	float prevY = 0;
	
	//Q update vars
	int state;
	int selectedAction = 0;
	
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
	    trueJumpCounter = 0;
	    trueSpeedCounter = 0;
	}
	
	/**
	 * check if there is a gap immediately in front of us
	 * @return: whether there is a gap immediately in front of us (true) or not (false)
	 */
	private boolean gapApproaching() {
	    if (getReceptiveFieldCellValue(marioCenter[0] + 1, marioCenter[1] + 1) == 0) {
	    	return true;
	    }
	    return false;
	}
	
	/**
	 * check if there is a wall 1 or 2 blocks in front of us
	 * @return: whether there is a wall in front of us (true) or not (false)
	 */
	private boolean wallApproaching() {
		if (getReceptiveFieldCellValue(marioCenter[0], marioCenter[1] + 1) != 0 || 
				getReceptiveFieldCellValue(marioCenter[0], marioCenter[1] + 2) != 0) {
	    	return true;
	    }
		return false;
	}
	
	/**
	 * check if there is an enemy 1 or 2 blocks in front of us
	 * @return: whether there is an enemy in front of us (true) or not (false)
	 */
	private boolean enemyApproaching() {
		for (int i = 0; i < enemiesFloatPos.length; i+=3) {
			float ex = enemiesFloatPos[i+1];
			//check if any enemies x vales are within a small area around mario
			if (ex <= 48 && ex >= -16) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * check if we moved down since last frame; if so, force trueJumpCounter to 0 and stop holding jump
	 * @return: whether or not we moved down since last frame and had trueJumpCounter > 1
	 */
	private boolean checkMovedDown() {
		//ignore trueJumpCounter <=1 so we can still wallkick (as during wallslide we are slowly moving down)
		if (marioFloatPos[1] > prevY && trueJumpCounter > 1) {
			trueJumpCounter = 0;
	    	action[Mario.KEY_JUMP] = false;
	    	return true;
		}
		return false;
	}
	
	private boolean movedUp() {
		return marioFloatPos[1] < prevY;
	}
	
	private boolean movedRight() {
		return marioFloatPos[0] > prevX;
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
	
	void printResult() {
        System.out.println("Print result");
        for (int i = 0; i < Q.length; i++) {
            System.out.print("out from " + stateNames[i] + ":  ");
            for (int j = 0; j < Q[i].length; j++) {
                System.out.print(df.format(Q[i][j]) + " ");
            }
            System.out.println();
        }
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
 
    /**
     * calculate the reward
     * @param s state
     * @param a action
     * @return corresponding reward
     */
    int R(int s, int a) {
    	//reward is a factor of new state reward value dependent on vert/horiz progress
    	int rewardMultiplicity = (movedUp()?1:0) + (movedRight()?2:0);
        return rewardMultiplicity * R[s][a];
    }
    
    /**
     * plug mario into one of our predefined states depending on environmental factors
     * @return the derived state
     */
    public int checkState() {
    	wAppro = wallApproaching(); 
		eAppro = enemyApproaching();
		gAppro = gapApproaching();
		movedDown = checkMovedDown();
		
		if (isMarioOnGround) {
			return (wAppro || eAppro || gAppro ? stateB : stateA);
		}
		if (movedDown) {
			return (eAppro ? stateF : stateE);
		}
		return (eAppro ? stateD : stateC);
    }
	
	public boolean[] getAction() {
		///update Q table with results of action selection from last tick
		int nextState = checkState();

        // Using this possible action, consider to go to the next state
        double q = Q(state, selectedAction);
        double maxQ = maxQ(nextState);
        int r = R(state, selectedAction);

        double value = q + alpha * (r + gamma * maxQ - q);
        setQ(state, selectedAction, value);

        //~start~
        state = checkState();
        //System.out.println("state: " + state);
        printResult();
    
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
        
        selectedAction = actionsFromState[index];
        action = actionInputPairs[state][selectedAction];
        
        //update persistent variables
 	    prevX = marioFloatPos[0];
 	    prevY = marioFloatPos[1]; 
        
	    //return final results
	    return action;
	}
}