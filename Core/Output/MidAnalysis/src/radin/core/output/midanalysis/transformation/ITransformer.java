package radin.core.output.midanalysis.transformation;

import java.util.*;

/**
 *
 * @param <T> Target type
 * @param <R> Relevant type
 */
public interface ITransformer<T, R extends Collection<T>> {
    
    T unScopeTo(T to);
    T unScopeTo();
    
    
    
    /**
     * Inserts a node at the beginning of the relevant layer
     * @param node non-null node
     * @return if the operation was successful
     */
    boolean insertFirst(T node);
    /**
     * Inserts a list of nodes at the beginning of the relevant layer
     * @param nodes a list of nodes, where no node is null
     * @return if the operation was successful
     */
    boolean insertFirst(List<? extends T> nodes);
    
    /**
     * Inserts a node at the end of the relevant layer
     * @param node a non-null node
     * @return if the operation was successful
     */
    boolean insertLast(T node);
    /**
     * Inserts a list of nodes at the end of the relevant layer
     * @param nodes a list of nodes, where no node is null
     * @return if the operation was successful
     */
    boolean insertLast(List<? extends T> nodes);
    
    /**
     * Inserts a node after a target node in the relevant layer
     * @param target either a node in the relevant layer or a null value. If the target is not null and is not within
     *              the layer, the operation fails. A null target value is equivalent to
     *              {@link ITransformer#insertLast(T)}.
     * @param node a non null node
     * @return if the target was valid and the operation succeeded
     */
    boolean insertAfter(T target, T node);
    /**
     * Inserts a node after a target node in the relevant layer
     * @param target either a node in the relevant layer or a null value. If the target is not null and is not within
     *              the layer, the operation fails. A null target value is equivalent to
     *              {@link ITransformer#insertLast(List)}.
     * @param nodes a list of nodes, where no node is null
     * @return if the target was valid and the operation succeeded
     */
    boolean insertAfter(T target, List<? extends T> nodes);
    
    /**
     * Inserts a node after a target node in the relevant layer
     * @param target either a node in the relevant layer or a null value. If the target is not null and is not within
     *              the layer, the operation fails. A null target value is equivalent to
     *              {@link ITransformer#insertFirst(T)}.
     * @param node a non null node
     * @return if the target was valid and the operation succeeded
     */
    boolean insertBefore(T target, T node);
    /**
     * Inserts a node after a target node in the relevant layer
     * @param target either a node in the relevant layer or a null value. If the target is not null and is not within
     *              the layer, the operation fails. A null target value is equivalent to
     *              {@link ITransformer#insertFirst(List)}.
     * @param nodes nodes a list of nodes, where no node is null
     * @return if the target was valid and the operation succeeded
     */
    boolean insertBefore(T target, List<? extends T> nodes);
    
    /**
     * Deletes a node from the relevant layer, returning false if the removal failed
     * @param node the node to remove, if it's null nothing happens and the operation "succeeds"
     * @return if the operation was successful
     */
    boolean delete(T node);
    
    /**
     * Deletes a set of nodes. If a delete fails, then entire thing fails. Doesn't guarantee that a failed operation
     * means that no nodes were deleted
     * @param nodes the nodes to delete
     * @return
     */
    default boolean delete(Set<? extends T> nodes) {
        return delete(nodes, false);
    }
    
    /**
     * Deletes a set of nodes. If a delete fails, then entire thing fails.
     * @param nodes the nodes to delete
     * @param safeDelete if true, it ensures that all nodes in {@code nodes} are within the relevant layer, otherwise
     *                  doesn't guarantee that a failed operation means nothing was deleted
     * @return if the operation was succesful
     */
    default boolean delete(Set<? extends T> nodes, boolean safeDelete) {
        if(safeDelete) {
            if(!getRelevant().containsAll(nodes)) return false;
        }
        for (T node : nodes) {
            if (!delete(node)) return false;
        }
        
        return true;
    }
    
    /**
     * Replaces the target node with another node
     * @param target a node within the relevant layer
     * @param node a new node to replace it with
     * @return if the operation was successful
     */
    default boolean replace(T target, T node) {
        T prev = previous(target);
        if(!insertAfter(prev, node)) return false;
        return delete(target);
    }
    /**
     * Replaces the target node with another node
     * @param target a node within the relevant layer
     * @param nodes a list of nodes to replace it with
     * @return if the operation was successful
     */
    default boolean replace(T target, List<? extends T> nodes) {
        T prev = previous(target);
        if(!insertAfter(prev, nodes)) return false;
        return delete(target);
    }
    
    
    boolean encapsulate(T target, T child);
    
    T next(T target);
    T previous(T target);
    R getRelevant();
    
    /**
     * Changes the scope somehow to be relevant to the target, and returns the old scope target
     * @param target the new target for the scope change. How this changes the relevant layer is implementation specific
     * @return the old target
     */
    T reScopeTo(T target);
    
    default boolean encapsulate(List<? extends T> targets, T newParent) throws TargetsNotConsecutive {
        if(!getRelevant().containsAll(targets)) return false;
        T node = targets.get(0);
        for (T target : targets) {
            if(target != node) throw new TargetsNotConsecutive();
            node = next(node);
        }
        T target = previous(targets.get(0));
        delete(new HashSet<>(targets));
        
        // newParent.addAllChildren(targets);
        if(target == null) {
            insertFirst(newParent);
        } else {
            insertAfter(target, newParent);
        }
        T oldScope = reScopeTo(newParent);
        insertFirst(targets);
        unScopeTo(oldScope);
        
        return true;
    }
    
    default boolean encapsulate(T newParent,
                                T target1, T... targets) throws TargetsNotConsecutive {
        if(targets.length == 0) {
            return encapsulate(target1, newParent);
        } else {
            List<T> list = Arrays.asList(targets);
            list.add(0, target1);
            return encapsulate(list, newParent);
        }
    }
    
    class TargetsNotConsecutive extends Exception { }
    
}
