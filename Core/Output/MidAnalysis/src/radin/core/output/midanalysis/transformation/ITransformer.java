package radin.core.output.midanalysis.transformation;

import radin.core.output.midanalysis.TypeAugmentedSemanticNode;

import java.util.*;

public interface ITransformer {
    
    /**
     * Inserts a node at the beginning of the relevant layer
     * @param node non-null node
     * @return if the operation was successful
     */
    boolean insertFirst(TypeAugmentedSemanticNode node);
    /**
     * Inserts a list of nodes at the beginning of the relevant layer
     * @param nodes a list of nodes, where no node is null
     * @return if the operation was successful
     */
    boolean insertFirst(List<? extends TypeAugmentedSemanticNode> nodes);
    
    /**
     * Inserts a node at the end of the relevant layer
     * @param node a non-null node
     * @return if the operation was successful
     */
    boolean insertLast(TypeAugmentedSemanticNode node);
    /**
     * Inserts a list of nodes at the end of the relevant layer
     * @param nodes a list of nodes, where no node is null
     * @return if the operation was successful
     */
    boolean insertLast(List<? extends TypeAugmentedSemanticNode> nodes);
    
    /**
     * Inserts a node after a target node in the relevant layer
     * @param target either a node in the relevant layer or a null value. If the target is not null and is not within
     *              the layer, the operation fails. A null target value is equivalent to
     *              {@link ITransformer#insertLast(TypeAugmentedSemanticNode)}.
     * @param node a non null node
     * @return if the target was valid and the operation succeeded
     */
    boolean insertAfter(TypeAugmentedSemanticNode target, TypeAugmentedSemanticNode node);
    /**
     * Inserts a node after a target node in the relevant layer
     * @param target either a node in the relevant layer or a null value. If the target is not null and is not within
     *              the layer, the operation fails. A null target value is equivalent to
     *              {@link ITransformer#insertLast(List)}.
     * @param nodes a list of nodes, where no node is null
     * @return if the target was valid and the operation succeeded
     */
    boolean insertAfter(TypeAugmentedSemanticNode target, List<? extends TypeAugmentedSemanticNode> nodes);
    
    /**
     * Inserts a node after a target node in the relevant layer
     * @param target either a node in the relevant layer or a null value. If the target is not null and is not within
     *              the layer, the operation fails. A null target value is equivalent to
     *              {@link ITransformer#insertFirst(TypeAugmentedSemanticNode)}.
     * @param node a non null node
     * @return if the target was valid and the operation succeeded
     */
    boolean insertBefore(TypeAugmentedSemanticNode target, TypeAugmentedSemanticNode node);
    /**
     * Inserts a node after a target node in the relevant layer
     * @param target either a node in the relevant layer or a null value. If the target is not null and is not within
     *              the layer, the operation fails. A null target value is equivalent to
     *              {@link ITransformer#insertFirst(List)}.
     * @param nodes nodes a list of nodes, where no node is null
     * @return if the target was valid and the operation succeeded
     */
    boolean insertBefore(TypeAugmentedSemanticNode target, List<? extends TypeAugmentedSemanticNode> nodes);
    
    /**
     * Deletes a node from the relevant layer, returning false if the removal failed
     * @param node the node to remove, if it's null nothing happens and the operation "succeeds"
     * @return if the operation was successful
     */
    boolean delete(TypeAugmentedSemanticNode node);
    
    /**
     * Deletes a set of nodes. If a delete fails, then entire thing fails. Doesn't guarantee that a failed operation
     * means that no nodes were deleted
     * @param nodes the nodes to delete
     * @return
     */
    default boolean delete(Set<? extends TypeAugmentedSemanticNode> nodes) {
        return delete(nodes, false);
    }
    
    /**
     * Deletes a set of nodes. If a delete fails, then entire thing fails.
     * @param nodes the nodes to delete
     * @param safeDelete if true, it ensures that all nodes in {@code nodes} are within the relevant layer, otherwise
     *                  doesn't guarantee that a failed operation means nothing was deleted
     * @return if the operation was succesful
     */
    default boolean delete(Set<? extends TypeAugmentedSemanticNode> nodes, boolean safeDelete) {
        if(safeDelete) {
            if(!getRelevant().containsAll(nodes)) return false;
        }
        for (TypeAugmentedSemanticNode node : nodes) {
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
    default boolean replace(TypeAugmentedSemanticNode target, TypeAugmentedSemanticNode node) {
        TypeAugmentedSemanticNode prev = previous(target);
        if(!insertAfter(prev, node)) return false;
        return delete(target);
    }
    /**
     * Replaces the target node with another node
     * @param target a node within the relevant layer
     * @param nodes a list of nodes to replace it with
     * @return if the operation was successful
     */
    default boolean replace(TypeAugmentedSemanticNode target, List<? extends TypeAugmentedSemanticNode> nodes) {
        TypeAugmentedSemanticNode prev = previous(target);
        if(!insertAfter(prev, nodes)) return false;
        return delete(target);
    }
    
    
    
    
    default boolean encapsulate(TypeAugmentedSemanticNode target, TypeAugmentedSemanticNode child) {
        if(!replace(target, child)) return false;
        child.addChild(target);
        return true;
    }
    
    TypeAugmentedSemanticNode next(TypeAugmentedSemanticNode target);
    TypeAugmentedSemanticNode previous(TypeAugmentedSemanticNode target);
    List<TypeAugmentedSemanticNode> getRelevant();
    
    default boolean encapsulate(List<? extends TypeAugmentedSemanticNode> targets, TypeAugmentedSemanticNode newParent) throws TargetsNotConsecutive {
        if(!getRelevant().containsAll(targets)) return false;
        TypeAugmentedSemanticNode node = targets.get(0);
        for (TypeAugmentedSemanticNode target : targets) {
            if(target != node) throw new TargetsNotConsecutive();
            node = next(node);
        }
        TypeAugmentedSemanticNode target = previous(targets.get(0));
        delete(new HashSet<>(targets));
        newParent.addAllChildren(targets);
        if(target == null) {
            insertFirst(newParent);
        } else {
            insertAfter(target, newParent);
        }
        return true;
    }
    
    default boolean encapsulate(TypeAugmentedSemanticNode newParent,
                                TypeAugmentedSemanticNode target1, TypeAugmentedSemanticNode... targets) throws TargetsNotConsecutive {
        if(targets.length == 0) {
            return encapsulate(target1, newParent);
        } else {
            List<TypeAugmentedSemanticNode> list = Arrays.asList(targets);
            list.add(0, target1);
            return encapsulate(list, newParent);
        }
    }
    
    class TargetsNotConsecutive extends Exception { }
    
}
