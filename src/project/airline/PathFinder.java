package project.airline;

import project.airport.Airport;

import java.util.*;

public class PathFinder {

    private HashMap<Integer, Airport> airports;
    public PathFinder(HashMap<Integer, Airport> airports) {
        this.airports = airports;
    }
    public Stack<Airport> generatePath(Airport s, Airport d){
        return disjktra(s,d);
    }
    class Edge {
        Airport orig,dest;
        double cost;

        public Edge(Airport orig, Airport dest, double cost) {
            this.orig = orig;
            this.dest = dest;
            this.cost = cost;
        }
        @Override
        public String toString() {

            return "("+orig+"->"+dest+")";
        }

    }

    // class used to compare edges in the priority queue
    class EdgeCmp implements Comparator<Edge> {

        HashMap<Airport, Double> dist;

        public EdgeCmp(HashMap<Airport, Double> dist) {
            this.dist = dist;
        }

        public int compare(Edge e1, Edge e2) {
            double v1 = dist.get(e1.orig) + e1.cost;
            double v2 =dist.get(e2.orig)  + e2.cost;
            return Double.compare(v1, v2);
        }


    }

    private Stack<Airport> disjktra(Airport s, Airport d) {
        HashMap<Airport, ArrayList<Edge>> g = new HashMap<>();
        ArrayList<Airport> ap = new ArrayList<>(airports.values());
        for(int i = 0; i< ap.size(); i++) {
            Airport fa = ap.get(i);
            for (Airport to : ap) {
                if (!to.equals(fa) && fa.getDistance(to) <= 14000) {
                    if (!g.containsKey(fa)) {
                        g.put(fa, new ArrayList<>());
                    }
                    g.get(fa).add(new Edge(fa, to, fa.getDistance(to)));
                }
            }
        }
        // initialize distances
        HashMap<Airport, Double> dist = new HashMap<>();
        HashMap<Airport, Airport> prev = new HashMap<>();
        HashMap<Airport, Boolean> visited = new HashMap<>();
        for(Airport tmp : g.keySet()) {
            dist.put(tmp, Double.POSITIVE_INFINITY);
            visited.put(tmp, false);
        }

        dist.put(s, 0d);
        visited.put(s, true);

        // initialize PQ
        PriorityQueue<Edge> Q = new PriorityQueue<>(new EdgeCmp(dist));

        for(Edge e : g.get(s)) Q.add(e);
        while(!Q.isEmpty()) {
            Edge mine = Q.poll();

            if (dist.get(mine.orig) + mine.cost < dist.get(mine.dest))
            {
                dist.put(mine.dest, dist.get(mine.orig) + mine.cost);
                prev.put(mine.dest, mine.orig);
                for(Edge e : g.get(mine.dest)) {
                    if(!visited.get(e.dest))
                        Q.add(e);
                }
            }

        }
        Stack<Airport> path = new Stack<>();
        for(Airport t = d; prev.get(t)!= null; t = prev.get(t) )
            path.push(t);

        return path;
    }
}
