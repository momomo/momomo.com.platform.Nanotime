package momomo.com.sources;

import momomo.com.annotations.informative.Development;
import momomo.com.$Lists;

/**
 * Also called exponential moving average. However, this description is more accurate.
 * 
 * Concurrently safe.
 * 
 * @see MovingAverageConverging#example()
 * 
 * @author Joseph S.
 */
public final class MovingAverageConverging extends MovingAverage {
    private final double α, β;
    
    /**
     * @param α how much weight is put on the next number. Higher is more, which will diminish old values.
     */
    public MovingAverageConverging(double α) {
        this.α = α;
        this.β = 1 - α;
    }
    
    protected final double add(double average, double number) {
        return β * average + α * number;
    }
    
    @Development private static void example() {
        double α = 0.8;
        
        MovingAverageConverging average = new MovingAverageConverging(α);
        
        for ( Integer number : $Lists.toList(1, 1000) ) {
            System.out.println(average.add(number));
        }
    }
    
}
