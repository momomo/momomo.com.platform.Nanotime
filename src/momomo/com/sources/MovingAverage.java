package momomo.com.sources;

/**
 * Concurrently safe.
 * 
 * Base class. 
 * 
 * @author Joseph S.
 */
public abstract class MovingAverage {
    private final Object lock    = new Object();
    public  final String prepend;
    
    protected double average = 0.0;
    
    protected MovingAverage() {
        this("Average\t");
    }
    
    protected MovingAverage(String prepend) {
        this.prepend = prepend;
    }
    
    protected abstract double add(double average, double number);
    
    public final double add(double number) {
        // Need to synchronize for Cumilative which updates local n.
        // AtomicDouble would be possible to use for Converging, but this is a universal solution
        synchronized (lock) {
            if (average == 0) average = number;
            
            return average = add(average, number);
        }
    }
    
    public final double get() {
        return average;
    }
    
    @Override
    public String toString() {
        return prepend + average;
    }
    
}

