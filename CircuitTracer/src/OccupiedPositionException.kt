/**
 * Thrown when an attempt is made to add a trace to an occupied position in a CircuitBoard
 * @author mvail
 */
class OccupiedPositionException(msg: String?) : RuntimeException(msg)