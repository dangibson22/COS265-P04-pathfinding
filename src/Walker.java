import java.util.Iterator;

/**
 * Walker takes an Iterable of Coords and simulates an individual
 * walking along the path over the given Terrain
 */
public class Walker {

    Queue<Coord> path;
    Coord loc;
    Coord nextLoc;
    Terrain terrain;
    float totalCost;
    float progress;
    int calls;

    // terrain: the Terrain the Walker traverses
    // path: the sequence of Coords the Walker follows
    public Walker(Terrain terrain, Iterable<Coord> path) {
        this.path = new Queue<>();
        for (Coord c : path) {
            this.path.enqueue(c);
        }
        this.loc = this.path.dequeue();
        this.nextLoc = this.path.peek();
        this.terrain = terrain;
        this.totalCost = 0.0f;
        this.progress = 0.0f;
        this.calls = 0;
    }

    // returns the Walker's current location
    public Coord getLocation() {
        return this.loc;
    }

    // returns true if Walker has reached the end Coord (last in path)
    public boolean doneWalking() {
        return this.path.isEmpty();
    }

    // advances the Walker along path
    // byTime: how long the Walker should traverse (may be any non-negative value)
    public void advance(float byTime) {
        calls++;
        float end = totalCost + byTime;
        while (totalCost < end) {
            float cost = terrain.computeTravelCost(loc, nextLoc);
            if (totalCost + (cost - progress) > end) {
                float prev = end - totalCost;
                totalCost += prev;
                progress += prev;
                break;
            } else {
                totalCost += (cost - progress);
                progress = 0.0f;
                loc = path.dequeue();
                if (!path.isEmpty()) nextLoc = path.peek();
                else {
                    nextLoc = null;
                    break;
                }
            }
        }
    }

}
