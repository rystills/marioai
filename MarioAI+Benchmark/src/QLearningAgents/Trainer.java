package QLearningAgents;

import ch.idsia.agents.Agent;
import ch.idsia.agents.learning.SmarterMLPAgent;
import ch.idsia.benchmark.tasks.Task;
import ch.idsia.evolution.EA;
import ch.idsia.evolution.Evolvable;

public class Trainer {
    public final Task task; //evaluation task (simulation)
    private final Agent agent;

    public Trainer(Task task, Agent agent) {
        this.agent = agent;
        this.task = task;
    }
    
    /**
     * run the agent through a simulation of the specified task
     */
    public float simulate() {
    	return task.evaluate(agent)[0];
    }
}