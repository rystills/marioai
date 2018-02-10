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
	
	private boolean DangerOfGap(byte[][] levelScene) {
	    int fromX = receptiveFieldWidth / 2;
	    int fromY = receptiveFieldHeight / 2;
	
	    if (fromX > 3)
	    {
	        fromX -= 2;
	    }
	
	    for (int x = fromX; x < receptiveFieldWidth; ++x)
	    {
	        boolean f = true;
	        for (int y = fromY; y < receptiveFieldHeight; ++y)
	        {
	            if (getReceptiveFieldCellValue(y, x) != 0)
	                f = false;
	        }
	        if (f ||
	                getReceptiveFieldCellValue(marioCenter[0] + 1, marioCenter[1]) == 0 ||
	                (marioState[1] > 0 &&
	                        (getReceptiveFieldCellValue(marioCenter[0] + 1, marioCenter[1] - 1) != 0 ||
	                                getReceptiveFieldCellValue(marioCenter[0] + 1, marioCenter[1]) != 0)))
	            return true;
	    }
	    return false;
	}
	
	private boolean DangerOfGap()
	{
	    return DangerOfGap(levelScene);
	}
	
	/**
	 * check if there is a gap immediately in front of us
	 * @param levelScenes: the 2d array containing level information
	 * @return: whether there is a gap immediately in front of us (true) or not (false)
	 */
	private boolean gapApproaching(byte[][] levelScenes) {
		int fromX = receptiveFieldWidth / 2;
	    int fromY = receptiveFieldHeight / 2;
	    if (getReceptiveFieldCellValue(marioCenter[0] - 1, marioCenter[1] + 1) == 0) {
	    	return true;
	    }
	    return false;
	}
	
	/**
	 * overloads gapApproaching to avoid requiring levelScene
	 * @return
	 */
	private boolean gapApproaching() {
		return gapApproaching(levelScene);
	}
	
	/**
	 * check if there is a wall 1 or 2 blocks in front of us
	 * @param levelScenes: the 2d array containing level information
	 * @return: whether there is a wall immediately in front of us (true) or not (false)
	 */
	private boolean wallApproaching(byte[][] levelScenes) {
		if (getReceptiveFieldCellValue(marioCenter[0], marioCenter[1] + 1) != 0 || 
				getReceptiveFieldCellValue(marioCenter[0], marioCenter[1] + 2) != 0) {
	    	return true;
	    }
		return false;
	}
	
	private boolean wallApproaching() {
		return wallApproaching(levelScene);
	}
	
	public boolean[] getAction() {
		//NOTE: the levelData is y,x; NOT x,y (ie. block to the right of mario is (center[0],center[1]+1), not (center[0]+1,center[1])
		
		//if there is a wall in our way, jump as high as we can
	    if (wallApproaching() && isMarioAbleToJump) {
	    	action[Mario.KEY_JUMP] = true;
	    }
	    if (action[Mario.KEY_JUMP]) {
		    if (++trueJumpCounter > 16) {
		        trueJumpCounter = 0;
		        action[Mario.KEY_JUMP] = false;
		    }
	    }
		    
	    action[Mario.KEY_RIGHT] = true;
	    
	    System.out.println("jump counter: " + trueJumpCounter);
	    
	    printSurroundings();
	    return action;
	}
}