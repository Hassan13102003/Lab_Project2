import java.util.Random;

public class Lab_Project {
    
    private static final int     NUMBER_OF_COMPONENTS = 30;
    private static final double  MIN_COMPONENT_VALUE  = -1;
    private static final double  MAX_COMPONENT_VALUE  = 1;
    private static final int     POPULATION_SIZE      = 30;
    private static final int     TOURNAMENT_SIZE      = 10;
    private static final double  MUTATION_PROBABILITY = 0.1;
    private static final double  SIGMA                = 0.9;
    private static final boolean USE_ELITIMS          = true;
    private static final int     MAX_GENERATIONS      = 10_000;
    
    private static final Random RAND = new Random();
    
    public static void main(String[] args) {
        double[][] population     = createPopulation();
        double[][] nextPopulation = new double[population.length][];
        double[]   fitness        = new double[population.length];
        
        int      bestSolutionIndex = -1;
        double   bestFitness       = 0;
        
        for (int generation = 1; generation <= MAX_GENERATIONS; generation++) {
            bestSolutionIndex = evaluatePopulation(population, fitness);
            bestFitness       = fitness[bestSolutionIndex];
            
            // System.out.printf("Generation #%d: %f\n", generation, bestFitness);
            
            if (USE_ELITIMS) {
                int secondBestSolutionIndex = -1;
                double secondBestFitness    =  0;
                for (int i = 0; i < population.length; i++) {
                    if (i != bestSolutionIndex && (secondBestSolutionIndex == -1 || fitness[i] > secondBestFitness)) {
                        secondBestFitness       = fitness[i];
                        secondBestSolutionIndex = i;
                    }
                }

                nextPopulation[0] = population[bestSolutionIndex];
                nextPopulation[1] = population[secondBestSolutionIndex];
            }
            
            for (int k = USE_ELITIMS ? 2 : 0; k < population.length; k++) {
                double[] firstParent  = select(population, fitness);
                double[] secondParent = select(population, fitness);
                nextPopulation[k]     = mutate(crossover(firstParent, secondParent));
            }
        
            double[][] tmp = population;
            population     = nextPopulation;
            nextPopulation = tmp;
        }
        
        System.out.println("\nSolution: " + doubleToString(population[bestSolutionIndex]));
        System.out.println("Fitness : " + bestFitness);
    }

    private static double[] mutate(double[] child) {
        boolean isMutated = false;
        
        for (int i = 0; i < child.length; i++) {
            if (RAND.nextDouble() <= MUTATION_PROBABILITY) {
                isMutated = true;
                child[i] += RAND.nextGaussian() * SIGMA;
                child[i]  = Math.max(child[i], MIN_COMPONENT_VALUE);
                child[i]  = Math.min(child[i], MAX_COMPONENT_VALUE);
            }
        }
        
        if (!isMutated) {
            int i     = RAND.nextInt(child.length);
            child[i] += RAND.nextGaussian() * SIGMA;
            child[i]  = Math.max(child[i], MIN_COMPONENT_VALUE);
            child[i]  = Math.min(child[i], MAX_COMPONENT_VALUE);
        }
        
        return child;
    }
    
    private static double[] crossover(double[] firstParent, double[] secondParent) {
        double[] child = new double[firstParent.length];

        for (int i = 0; i < child.length; i++) {
            double min = Math.min(firstParent[i], secondParent[i]);
            double max = Math.max(firstParent[i], secondParent[i]);
            child[i]   = min + RAND.nextDouble() * (max - min);
        }

        return child;
    }
    
    private static double[] select(double[][] population, double[] fitness) {
        int bestSolutionIndex = -1;

        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            int randomIndex = RAND.nextInt(population.length);
            if (bestSolutionIndex == -1 || fitness[randomIndex] > fitness[bestSolutionIndex]) {
                bestSolutionIndex = randomIndex;
            }
        }

        return population[bestSolutionIndex];
    }
    
    private static double calculateFitness(double[] individual) {
        int D        = individual.length;
        double value = 0;
        
        for (int i = 0; i < D; i++) {
            double x = individual[i];
            value   += Math.pow(x, 2) - 10 * Math.cos(2 * Math.PI * x);
        }
        
        return -1.0 * (10 * D + value);
    }
    
    private static int evaluatePopulation(double[][] population, double[] fitness) {
        double bestFitness      =  -1.0 * Double.MAX_VALUE;
        int    bestFitnessIndex =  -1;
        
        for (int i = 0; i < population.length; i++) {
            fitness[i] = calculateFitness(population[i]);
            if (bestFitnessIndex == -1 || fitness[i] >= bestFitness) {
                bestFitness = fitness[i];
                bestFitnessIndex = i;
            }
        }
        
        return bestFitnessIndex;
    }
    
    private static double[][] createPopulation() {
        double[][] population = new double[POPULATION_SIZE][];
        double[]   individual;
        
        for (int i = 0; i < population.length; i++) {
            individual = new double[NUMBER_OF_COMPONENTS];
            for (int k = 0; k < individual.length; k++) {
                individual[k] = MIN_COMPONENT_VALUE + RAND.nextDouble() * (MAX_COMPONENT_VALUE - MIN_COMPONENT_VALUE);
            }
            population[i] = individual;
        }
        
        return population;
    }
    
    private static String doubleToString(double[] array) {
        StringBuilder builder = new StringBuilder("[");
        
        for (int i = 0; i < array.length - 1; i++) {
            builder.append(array[i]).append(", ");
        }
        builder.append(array[array.length - 1]).append("]");
        
        return builder.toString();
    }
}