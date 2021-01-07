import java.lang.IndexOutOfBoundsException;
import java.lang.IllegalArgumentException;

/**
 * Pathfinder uses A* search to find a near optimal path
 * between to locations with given terrain.
 */

public class Pathfinder {

    private float h;
    private int N;
    private int searchSize;
    private boolean pathFound;
    private Terrain terrain;
    private Coord start;
    private Coord end;
    private MinPQ<PFNode> pq;
    private PFNode[][] board;
    private PFNode endNode;
    private Stack<Coord> path;

    /**
     * PFNode will be the key for MinPQ (used in computePath())
     */

    private class PFNode implements Comparable<PFNode> {
        private Coord loc;
        private PFNode fromNode;
        private float cost;

        public PFNode(Coord loc, PFNode fromNode) {
            this.loc = loc;
            this.fromNode = fromNode;
            computeCost(h);
        }

        // compares this with that, used to find minimum cost PFNode
        public int compareTo(PFNode that) {
            if      (this.getCost(h) < that.getCost(h)) return -1;
            else if (this.getCost(h) > that.getCost(h)) return 1;
            else return 0;
        }

        // returns the cost to travel from starting point to this
        // via the fromNode chain

        // Returns the location of the PFNode
        private Coord getLoc() {
            return loc;
        }

        private void computeCost(float heuristic) {
            if (validateCoord(this.loc)) {
                PFNode b = fromNode;
                if (b == null) this.cost = 0.0f;
                else {
                    float nextCost = terrain.computeTravelCost(b.getLoc(), loc);
                    this.cost = b.cost + nextCost;
                }
            }
        }

        public float getCost(float heuristic) { return cost + heuristic*terrain.computeDistance(loc, end); }

        // returns an Iterable of PFNodes that surround this
        public Iterable<PFNode> neighbors() {
            Coord[] coords = findNeighbors(loc);
            Stack<PFNode> s = new Stack<>();
            for (Coord c : coords) {
                s.push(new PFNode(c, this));
            }
            return s;
        }
    }

    public Pathfinder(Terrain terrain) {
        start = null;
        end = null;
        pq = new MinPQ<>();
        this.terrain = terrain;
        N = this.terrain.getN();
        h = 0.0f;
        searchSize = 0;
        pathFound = false;
        board = new PFNode[N][N];
    }

    public void setPathStart(Coord loc) {
        if (loc == null) throw new IllegalArgumentException("Error: start input coord is null");
        if (!validateCoord(loc)) throw new IndexOutOfBoundsException("Error: start coord is out of bounds");
        start = new Coord(loc.getI(), loc.getJ());
        start = loc;
    }

    public Coord getPathStart() {
        return start;
    }

    public void setPathEnd(Coord loc) {
        if (loc == null) throw new IllegalArgumentException("Error: end input coord is null");
        if (!validateCoord(loc)) throw new IndexOutOfBoundsException("Error: end coord is out of bounds");
        end = new Coord(loc.getI(), loc.getJ());
        end = loc;
    }

    public Coord getPathEnd() {
        return end;
    }

    public void setHeuristic(float v) {
        this.h = v;
    }

    public float getHeuristic() {
        return this.h;
    }

    public void resetPath() {
        pq = new MinPQ<>();
        N = this.terrain.getN();
        board = new PFNode[N][N];
        searchSize = 0;
        path = null;
    }

    // Helper function to find neighbors given a coord
    private Coord[] findNeighbors(Coord s) {
        Coord a = new Coord(s.getI(), s.getJ()-1);
        Coord b = new Coord(s.getI()+1, s.getJ());
        Coord c = new Coord(s.getI(), s.getJ()+1);
        Coord d = new Coord(s.getI()-1, s.getJ());
        // a is 1 up, b is 1 right, c is 1 down, d is 1 left
        Coord[] e = new Coord[4];
        e[0] = a;
        e[1] = b;
        e[2] = c;
        e[3] = d;
        return e;
    }

    public void computePath() {
        if (start == null) throw new IllegalArgumentException("Error: start has not been set");
        if (end == null) throw new IllegalArgumentException("Error: end has not been set");
        Coord currentLoc = getPathStart();
        boolean atStart = true;
        boolean found = false;
        while (true) {
            PFNode currentNode;
            if (atStart) currentNode = new PFNode(currentLoc, null);
            else {
                if (pq.isEmpty()) break;
                else {
                    currentNode = pq.delMin();
                    currentLoc = currentNode.getLoc();
                }
            }
            for (PFNode c : currentNode.neighbors()) {
                if (validateCoord(c.getLoc())) {
                    if (c.getLoc().equals(getPathEnd())) {
                        currentNode = c;
                        found = true;
                        break;
                    }
                    if (board[c.getLoc().getI()][c.getLoc().getJ()] == null) {
                        pq.insert(c);
                        board[c.getLoc().getI()][c.getLoc().getJ()] = c;
                        searchSize++;
                    } else if (c.getCost(h) < board[c.getLoc().getI()][c.getLoc().getJ()].getCost(h)) {
                        pq.insert(c);
                        board[c.getLoc().getI()][c.getLoc().getJ()] = c;
                    }
                }
            }
            if (found) {
                pathFound = true;
                endNode = currentNode;
                PFNode c = endNode;
                path = new Stack<>();
                while (c != null) {
                    path.push(c.getLoc());
                    c = c.fromNode;
                }
                break;
            }
            atStart = false;
        }
    }

    public boolean validateCoord(Coord c) {
        int ci = c.getI();
        int cj = c.getJ();
        return ci >= 0 && ci < N && cj >= 0 && cj < N;
    }

    public boolean foundPath() {
        return pathFound;
    }

    public float getPathCost() {
        PFNode c = endNode;
        if (c != null) return c.getCost(0);
        else return 0;
    }

    public int getSearchSize() {
        return searchSize;
    }

    public Iterable<Coord> getPathSolution() {
        return path;
    }

    public boolean wasSearched(Coord loc) {
        return board[loc.getI()][loc.getJ()] != null;
    }
}
