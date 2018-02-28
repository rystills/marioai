package ch.idsia.evolution.ea;

import ch.idsia.agents.Agent;
import ch.idsia.agents.learning.SmarterMLPAgent;
import ch.idsia.benchmark.tasks.Task;
import ch.idsia.evolution.EA;
import ch.idsia.evolution.Evolvable;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: Apr 29, 2009
 * Time: 12:16:49 PM
 */
public class SmarterES implements EA
{

    private final Evolvable[] population;
    private final float[] fitness;
    private final int elite;
    private final Task task;
    private final int evaluationRepetitions = 1;

    public SmarterES(Task task, Evolvable initial, int populationSize, int numParents)
    {
        this.population = new Evolvable[populationSize];
        for (int i = 0; i < population.length; i++)
        {
            population[i] = initial.getNewInstance();
        }
        this.fitness = new float[populationSize];
        this.elite = numParents;
        this.task = task;
    }

    public void nextGeneration(float mutationMagnitude)
    {
        for (int i = 0; i < elite; i++)
        {
            evaluate(i);
        }
        for (int i = elite; i < population.length; i++)
        {
            population[i] = population[i - elite].copy();
            SmarterMLPAgent sm = (SmarterMLPAgent)population[i];
            sm.mutate(mutationMagnitude);
            evaluate(i);
        }
        shuffle();
        sortPopulationByFitness();
    }
    
    public void nextGeneration() {
    	nextGeneration(-1);
    }

    private void evaluate(int which)
    {
        fitness[which] = 0;
        for (int i = 0; i < evaluationRepetitions; i++)
        {
            population[which].reset();
            fitness[which] += task.evaluate((Agent) population[which])[0];
//            System.out.println("which " + which + " fitness " + fitness[which]);
        }
        fitness[which] = fitness[which] / evaluationRepetitions;
    }

    private void shuffle()
    {
        for (int i = 0; i < population.length; i++)
        {
            swap(i, (int) (Math.random() * population.length));
        }
    }

    private void sortPopulationByFitness()
    {
        for (int i = 0; i < population.length; i++)
        {
            for (int j = i + 1; j < population.length; j++)
            {
                if (fitness[i] < fitness[j])
                {
                    swap(i, j);
                }
            }
        }
    }

    private void swap(int i, int j)
    {
        float cache = fitness[i];
        fitness[i] = fitness[j];
        fitness[j] = cache;
        Evolvable gcache = population[i];
        population[i] = population[j];
        population[j] = gcache;
    }

    public Evolvable[] getBests()
    {
        return new Evolvable[]{population[0]};
    }

    public float[] getBestFitnesses()
    {
        return new float[]{fitness[0]};
    }

}
