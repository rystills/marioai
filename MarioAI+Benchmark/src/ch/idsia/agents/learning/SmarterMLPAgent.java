package ch.idsia.agents.learning;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.evolution.Evolvable;
import ch.idsia.evolution.SmarterMLP;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: Apr 28, 2009
 * Time: 2:09:42 PM
 */
public class SmarterMLPAgent implements Agent, Evolvable
{

    public SmarterMLP mlp;
    private String name = "SimpleMLPAgent";
    final int numberOfOutputs = 6;
    final int numberOfInputs = 10;
    private Environment environment;

    /*final*/
    protected byte[][] levelScene;
    /*final */
    protected byte[][] enemies;
    protected byte[][] mergedObservation;

    protected float[] marioFloatPos = null;
    protected float[] enemiesFloatPos = null;

    protected int[] marioState = null;

    protected int marioStatus;
    protected int marioMode;
    protected boolean isMarioOnGround;
    protected boolean isMarioAbleToJump;
    protected boolean isMarioAbleToShoot;
    protected boolean isMarioCarrying;
    protected int getKillsTotal;
    protected int getKillsByFire;
    protected int getKillsByStomp;
    protected int getKillsByShell;

    // values of these variables could be changed during the Agent-Environment interaction.
    // Use them to get more detailed or less detailed description of the level.
    // for information see documentation for the benchmark <link: marioai.org/marioaibenchmark/zLevels
    int zLevelScene = 1;
    int zLevelEnemies = 0;

	//persistent variables for stateful logic
    float prevY = 0;
    protected int[] marioCenter;

    public SmarterMLPAgent()
    {
        mlp = new SmarterMLP(numberOfInputs, 10, numberOfOutputs);
    }

    private SmarterMLPAgent(SmarterMLP mlp)
    {
        this.mlp = mlp;
    }

    public Evolvable getNewInstance()
    {
        return new SmarterMLPAgent(mlp.getNewInstance());
    }

    public Evolvable copy()
    {
        return new SmarterMLPAgent(mlp.copy());
    }

    public void integrateObservation(Environment environment)
    {
        this.environment = environment;
        levelScene = environment.getLevelSceneObservationZ(zLevelScene);
        enemies = environment.getEnemiesObservationZ(zLevelEnemies);
        mergedObservation = environment.getMergedObservationZZ(1, 0);

        this.marioFloatPos = environment.getMarioFloatPos();
        this.enemiesFloatPos = environment.getEnemiesFloatPos();
        this.marioState = environment.getMarioState();

        // It also possible to use direct methods from Environment interface.
        //
        marioStatus = marioState[0];
        marioMode = marioState[1];
        isMarioOnGround = marioState[2] == 1;
        isMarioAbleToJump = marioState[3] == 1;
        isMarioAbleToShoot = marioState[4] == 1;
        isMarioCarrying = marioState[5] == 1;
        getKillsTotal = marioState[6];
        getKillsByFire = marioState[7];
        getKillsByStomp = marioState[8];
        getKillsByShell = marioState[9];
    }

    public void giveIntermediateReward(float intermediateReward)
    {

    }

    public void reset()
    { mlp.reset(); }

    public void mutate()
    { mlp.mutate(); }
    
    public void mutate(float mutationMagnitude) {
    	mlp.mutate(mutationMagnitude);
    }
    
    public void recombine(SmarterMLPAgent parent1, SmarterMLPAgent parent2) {
    	//TODO: consider using another mlp for last, rather than the current mlp (which is still just a fresh copy of an elite)
    	this.mlp.psoRecombine(this.mlp, parent1.mlp, parent2.mlp);
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
		if (marioFloatPos[1] > prevY) {
	    	return true;
		}
		return false;
	}
	
	/**
	 * check if we are about to stomp on an enemy; if so, hold jump
	 * @return: whether we are about to stomp on an enemy (true) or not (false)
	 */
	private boolean checkAboutToStomp() {
		//can't stomp an enemy while grouhnded
		if (isMarioOnGround) {
			return false;
		}
		//check if any enemies are within a tight bound of our x position (note that we ignore y position for simplicity)
		for (int i = 0; i < enemiesFloatPos.length; i+=3) {
			if (enemiesFloatPos[i+1] <= 16 && enemiesFloatPos[i+1] >= -8) {
		    	return true;
			}
		}
    	return false;
	}
    
	public int getReceptiveFieldCellValue(int x, int y)
	{
	    if (x < 0 || x >= levelScene.length || y < 0 || y >= levelScene[0].length)
	        return 0;

	    return levelScene[x][y];
	}
	
    public boolean[] getAction()
    {
//        double[] inputs = new double[]{probe(-1, -1, levelScene), probe(0, -1, levelScene), probe(1, -1, levelScene),
//                              probe(-1, 0, levelScene), probe(0, 0, levelScene), probe(1, 0, levelScene),
//                                probe(-1, 1, levelScene), probe(0, 1, levelScene), probe(1, 1, levelScene),
//                                1};
       
    	/*double[] inputs = new double[]{probe(-1, -1, mergedObservation), probe(0, -1, mergedObservation), probe(1, -1, mergedObservation),
                probe(-1, 0, mergedObservation), probe(0, 0, mergedObservation), probe(1, 0, mergedObservation),
                probe(-1, 1, mergedObservation), probe(0, 1, mergedObservation), probe(1, 1, mergedObservation),
                1};*/
    	
    	marioCenter = environment.getMarioReceptiveFieldCenter();
    	
    	double[] inputs = new double[] {
    			gapApproaching() ? 1 : 0,
    	    	wallApproaching() ? 1 : 0,
    			enemyApproaching() ? 1 : 0,
    			checkAboutToStomp() ? 1 : 0,
    			checkMovedDown() ? 1 : 0,
    			marioMode == 2 ? 1 : 0,
    			isMarioOnGround ? 1 : 0,
    			isMarioAbleToJump ? 1 : 0
    	};
        double[] outputs = mlp.propagate(inputs);
        boolean[] action = new boolean[numberOfOutputs];
        for (int i = 0; i < action.length; i++) {
            action[i] = outputs[i] > 0;
        }
        
        //update persistent variables
	    prevY = marioFloatPos[1];
	    
        return action;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    private double probe(int x, int y, byte[][] scene)
    {
        int realX = x + 11;
        int realY = y + 11;
        return (scene[realX][realY] != 0) ? 1 : 0;
    }
}
