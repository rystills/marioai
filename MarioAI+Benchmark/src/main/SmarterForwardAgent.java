package main;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment; 

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, sergey.karakovskiy@gmail.com
 * Date: Apr 8, 2009
 * Time: 4:03:46 AM
 */

public class SmarterForwardAgent extends BasicMarioAIAgent implements Agent {
	int trueJumpCounter = 0;
	int trueSpeedCounter = 0;
	
	public SmarterForwardAgent() {
	    super("ForwardAgent");
	    reset();
	}
	
	public void reset() {
	    action = new boolean[Environment.numberOfButtons];
	    action[Mario.KEY_RIGHT] = true;
	    action[Mario.KEY_SPEED] = true;
	    trueJumpCounter = 0;
	    trueSpeedCounter = 0;
	}
	
	/**
	 * print a single row from our surroundings
	 * @param numTilesOut: how many tiles away from mario we should display
	 * @param row: the relative row offset we wish to display
	 */
	private void printSurroundingsRow(int numTilesOut, int row) {
		System.out.print('|');
		for (int i = numTilesOut; i > 0; --i) {
			String curVal = Integer.toString(getReceptiveFieldCellValue(marioCenter[0] - row, marioCenter[1] - i));
			if (curVal.length() < 3) {
				curVal = ' ' + curVal + ' ';
			}
			System.out.print(curVal + "|");
		}
		for (int i = 0; i < numTilesOut+1; ++i) {
			String curVal = Integer.toString(getReceptiveFieldCellValue(marioCenter[0] - row, marioCenter[1] + i));
			if (curVal.length() < 3) {
				curVal = ' ' + curVal + ' ';
			}
			System.out.print(curVal + "|");
		}
		System.out.print('\n');	
	}
	
	/**
	 * print our surrounds
	 * @param numTilesOut: how many tiles away from mario we should display
	 */
	private void printSurroundings(int numTilesOut) {
		for (int i = 0; i < numTilesOut*5; ++i) {
			System.out.print('-');
		}
		System.out.print('\n');
		for (int i = numTilesOut; i > 0; --i) {
			printSurroundingsRow(numTilesOut, i);
		}
		for (int i = 0; i < numTilesOut+1; ++i) {
			printSurroundingsRow(numTilesOut, -i);
		}
		for (int i = 0; i < numTilesOut*5; ++i) {
			System.out.print('-');
		}
		System.out.print('\n');
	}
	
	private void printSurroundings() {
		printSurroundings(2);
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
//		System.out.println("mario x: " + marioFloatPos[0] + ", mario y: " + marioFloatPos[1]);
//		for (int i = 0; i < enemiesFloatPos.length; i+=3) {
//			System.out.println("x: " + enemiesFloatPos[i+1] + ", y: " + enemiesFloatPos[i+2]);
//		}
		for (int i = 0; i < enemiesFloatPos.length; i+=3) {
			float ex = enemiesFloatPos[i+1];
			//check if any enemies x vales are within a small area around mario
			if (ex <= 40 && ex >= -16) {
				System.out.println("enemy approaching; jumping");
				return true;
			}
		}
		return false;
	}
	
	/**
	 * check whether or not we can currently shoot a fireball
	 */
	private boolean checkShootFireball() {
		//TODO: isMarioAbleToShoot only returns true when KEY_SPEED is not down, making it useless
		//LevelScene.fireballsOnScreen is what we need, but no way to access it without modifying source
		if (isMarioAbleToShoot) {
			action[Mario.KEY_SPEED] = !action[Mario.KEY_SPEED];
			return true;
		}
		return false;
	}
	
	public boolean[] getAction() {
		//NOTE: the levelData is y,x; NOT x,y (ie. block to the right of mario is (center[0],center[1]+1), not (center[0]+1,center[1])
		
		//if there is a wall in our way, jump
	    if ((isMarioAbleToJump) && (wallApproaching() || gapApproaching() || enemyApproaching())) {
	    	action[Mario.KEY_JUMP] = true;
	    }
	    if (action[Mario.KEY_JUMP]) {
	    	//while jumping, monitor frames held; once 16 is passed, release to prepare for future jump
		    if (++trueJumpCounter > 16) {
		        trueJumpCounter = 0;
		        action[Mario.KEY_JUMP] = false;
		    }
	    }
	    //toggle jump off if we hit the ground before getting the full height out of our jump
	    if (isMarioOnGround && trueJumpCounter > 1) {
	    	trueJumpCounter = 0;
	    	action[Mario.KEY_JUMP] = false;
	    }
	    
	    checkShootFireball();
	    
	    //TODO: check slow down or shorten jump to avoid multiple small gaps (tryLandEarly)
		    
	    action[Mario.KEY_RIGHT] = true;
	    
	    //System.out.println("jump counter: " + trueJumpCounter);
	    
	    //printSurroundings();
	    //System.out.println("gap approaching: " + gapApproaching());
	    return action;
	}
}