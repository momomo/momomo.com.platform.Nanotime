package momomo.com.annotations.informative;

/**
 * Means once created and pushed for use, it is not intended to be updated, nor should it be.
 * 
 * Used to inform this intention on hibernate entities albeit there is also the Hibernate annotation @Immutable but with unknown side effects.
 * 
 * Use of this is deemed informative and not hardly enforced allowing us to remove it should be desire to modify the used intent.
 *
 * @author Joseph S.
 */
@Private
public @interface Immuted {}
