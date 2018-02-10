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

public class SmarterForwardAgent extends BasicMarioAIAgent implements Agent
{
int trueJumpCounter = 0;
int trueSpeedCounter = 0;

public SmarterForwardAgent()
{
    super("ForwardAgent");
    reset();
}

public void reset()
{
    action = new boolean[Environment.numberOfButtons];
    action[Mario.KEY_RIGHT] = true;
    action[Mario.KEY_SPEED] = true;
    trueJumpCounter = 0;
    trueSpeedCounter = 0;
}

private boolean DangerOfGap(byte[][] levelScene)
{
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
    if (getReceptiveFieldCellValue(marioCenter[0], marioCenter[1] + 1) == 0) {
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
 * check if there is a wall immediately in front of us
 * @param levelScenes: the 2d array containing level information
 * @return: whether there is a wall immediately in front of us (true) or not (false)
 */
private boolean wallApproaching(byte[][] levelScenes) {
	if (getReceptiveFieldCellValue(marioCenter[0] + 1, marioCenter[1]) == 0) {
		//System.out.println("wall approaching: true");
    	return true;
    }
	System.out.println("wall approaching: false");
	return false;
}

/**
 * print the current state of the levelScene 2dArray
 */
private void printLevel() {
	System.out.println("levelPrintStart");
	//System.out.println("player pos: " + marioCenter[0] + ", " + marioCenter[1]);
	for (int i = 0; i < levelScene.length; ++i) {
		String lineStr  ="";
		for (int r = 0; r < levelScene[i].length; ++r) {
			lineStr += levelScene[i][r];
		}
		System.out.println(lineStr);
	}
	System.out.println("levelPrintEnd");
}

/**
 * overloads wallApproaching to avoid requiring levelScene
 * @return
 */
private boolean wallApproaching() {
	return wallApproaching(levelScene);
}

public boolean[] getAction()
{
    // this Agent requires observation integrated in advance.

    if (gapApproaching() || wallApproaching()) {
    	action[Mario.KEY_JUMP] = true;
    }
    else {
    	action[Mario.KEY_JUMP] = false;
    }
    
    action[Mario.KEY_JUMP] = !action[Mario.KEY_JUMP];
    action[Mario.KEY_RIGHT] = true;
    System.out.println(action);
    //printLevel();
    return action;
}
}