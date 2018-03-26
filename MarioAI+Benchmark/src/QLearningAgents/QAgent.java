package QLearningAgents;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.LevelScene;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment; 
import ch.idsia.benchmark.mario.environments.MarioEnvironment;

public class QAgent extends BasicMarioAIAgent implements Agent {
	
	public LevelScene lvl;
	public LevelScene oldlvl;
	public LevelScene newlvl;
	public int counter = 0;
	
	public QAgent() {
	    super("QAgent");
	    reset();
	}
	
	public void integrateObservation(Environment environment) {
		super.integrateObservation(environment);
		lvl = ((MarioEnvironment)environment).levelScene;
		//check one step ahead for a wall (clone test)
		try {
			newlvl = (LevelScene)lvl.clone();
		} catch (CloneNotSupportedException e) { }
		//jump if there will be a block in front of us in the future
		levelScene = newlvl.getLevelSceneObservationZ(1);
		//System.out.println("before: " + newlvl.getEnemiesFloatPos()[1]);
//		System.out.println("before: " + levelScene[marioCenter[0]][marioCenter[1]+2]);
		newlvl.tick();
		levelScene = newlvl.getLevelSceneObservationZ(1);
		//System.out.println("after: " + newlvl.getEnemiesFloatPos()[1]);
//		System.out.println("after: " + levelScene[marioCenter[0]][marioCenter[1]+2]);
		if (levelScene[marioCenter[0]][marioCenter[1]+1] != 0) {
			action[Mario.KEY_JUMP] = !action[Mario.KEY_JUMP];
		};
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