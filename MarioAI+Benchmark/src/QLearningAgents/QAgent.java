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
	public int counter = 0;
	
	public QAgent() {
	    super("QAgent");
	    reset();
	}
	
	public void integrateObservation(Environment environment) {
		lvl = ((MarioEnvironment)environment).levelScene;
		if (counter == 0) {
			//Java why :(
			try {
				oldlvl = (LevelScene)((MarioEnvironment)environment).levelScene.clone();
			} catch (CloneNotSupportedException e) { }
		}
		if (++counter == 10) {
			counter = 0;
			try {
				lvl= (LevelScene)oldlvl.clone();
			} catch (CloneNotSupportedException e) { }
			((MarioEnvironment)environment).levelScene = lvl;
		}
	}
	
	public void reset() {
		 action = new boolean[Environment.numberOfButtons];
		 action[Mario.KEY_RIGHT] = true;
	}
	
	public boolean[] getAction() {
		return action;
	}
}