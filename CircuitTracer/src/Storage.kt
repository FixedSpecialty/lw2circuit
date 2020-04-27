import java.util.*

/** A container for storing elements of type T in one of several
 * possible underlying data structures.
 * Additional data structures (or variations on data structures)
 * can be added by adding to the DataStructure enum values and
 * adding corresponding cases to wrapper methods.
 * This is an example of a design pattern known as a Bridge,
 * that allows users to interact with potentially many different
 * classes through a common interface.
 *
 * @author CS221
 */
class Storage<T>(
    /** the data structure chosen for this Storage to use  */
    private val dataStructure: DataStructure
) {
    /** supported underlying data structures for Storage to use  */
    enum class DataStructure {
        Stack, Queue
    }

    /** the data structures - only one will be instantiated and used   */
    private var queue: Queue<T>? = null
    private var stack: Stack<T>? = null

    /** Add element to underlying data structure
     * @param element T to store
     */
    /** Constructor
     * @param dataStructure choice of DataStructures
     */
//    constructor(dataStructure: T) {
//        when (dataStructure) {
//            DataStructure.Stack -> stack = Stack()
//            DataStructure.Queue -> queue = LinkedList()
//        }
//    }

    fun store(element: T) {
        when (dataStructure) {
            DataStructure.Stack -> stack!!.push(element)
            DataStructure.Queue -> queue!!.add(element)
        }
    }

    /** Remove and return the next T from storage
     * @return next T from storage
     */
    fun retrieve(): T? {
        return when (dataStructure) {
            DataStructure.Stack -> stack!!.pop()
            DataStructure.Queue -> queue!!.remove()
        }
    }

    /** @return true if store is empty, else false
     */
    fun isEmpty(): Boolean {
        return when (dataStructure) {
            DataStructure.Stack -> stack!!.isEmpty()
            DataStructure.Queue -> queue!!.isEmpty()
        }
    }

        /** @return size of store
         */
        fun size(): Int {
            var size = 0
            size = when (dataStructure) {
                DataStructure.Stack -> stack!!.size
                DataStructure.Queue -> queue!!.size
            }
            return size
        }
            /** Alternative to using the constructor returns
             * a Storage already configured to use a Stack
             * @return instance of Storage configured to use a Stack
             */
            fun <E> getStackInstance(): Storage<E> {
                return Storage(DataStructure.Stack)
            }

            /** Alternative to using the constructor returns
             * a Storage already configured to use a Queue
             * @return instance of Storage configured to use a Queue
             */
            fun <E> getQueueInstance(): Storage<E> {
                return Storage(DataStructure.Queue)
            }
    }
