import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Deque;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

import it.unimi.dsi.webgraph.ImmutableGraph;

public class Influence_Maximizer {
    ImmutableGraph G;
    int n;
    long m;
    double eps = .1;
    double p; // given probability of arc existence
    int k = 5; // given number of maximum influence nodes
    double W;
    double sketchTime, seedTime;

    int[] permutation;
    BitSet marked;

    public static void main(String[] args) throws Exception {

        int[] betas = {1, 2};

        System.out.println("\nAlgorithm: List");
        System.out.println("====================");

        for(int beta : betas) {

            long startTime = System.currentTimeMillis();

            Influence_Maximizer iml = new Influence_Maximizer("/Users/akshaykhot/Desktop/Thesis/infl_max/sym-noself/cnr-2000-t", 0.1, beta);

            iml.maximize();

            long estimatedTime = System.currentTimeMillis() - startTime;

            System.out.println("Total time = " + estimatedTime / 1000.0 + " sec");
            System.out.println("---------------------------------------------------");
        }
    }

    public Influence_Maximizer(String basename, Double  p, int beta) throws Exception {
        G = ImmutableGraph.load(basename);

        n = G.numNodes();
        m = G.numArcs();
        W = beta * (n + m) * Math.log(n);


        System.out.println("\nK: " + k);
        System.out.println("Beta: " + beta);
        System.out.println("n=" + n + ", m=" + m  + ", W=" + W);

        marked = new BitSet(n);

        permutation = new int[n];

        for(int i=0; i<n; i++)
            permutation[i] = i;
        Random rnd = new Random();
        for (int i=n; i>1; i--) {
            int j = rnd.nextInt(i);
            int temp = permutation[i-1];
            permutation[i-1] = permutation[j];
            permutation[j] = temp;
        }

        this.p = p;
    }

    public void maximize() {

        long start, time;

        start = System.currentTimeMillis();
        Sketch_Generator sketcher = new Sketch_Generator(this);
        sketcher.get_sketch();
        time = System.currentTimeMillis() - start;
        this.sketchTime = time/1000.0;


        start = System.currentTimeMillis();
        Seed_Computer seeder = new Seed_Computer(this);
        seeder.get_seeds(sketcher.I, k, sketcher.sketch_num, 0);
        time = System.currentTimeMillis() - start;
        this.seedTime = time/1000.0;


        System.out.println("\nsketches_creation_time = " + this.sketchTime + " sec");
        System.out.println("compute_seeds_time = " + this.seedTime + " sec");
    }
}
