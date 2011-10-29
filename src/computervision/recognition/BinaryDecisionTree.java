/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.recognition;

/**
 *
 * @author Malik Ahmed
 */
public class BinaryDecisionTree<T>
{
    public static class Node<T>
    {
        private Node<T> parent = null;
        private Decision decision = null;
        private T answer = null;
        private Node<T> yes = null, no = null;

        public Node(Node<T> parent, Decision decision)
        {
            this.parent = parent;
            this.decision = decision;
//            this.yes = onYes;
//            this.no = onNo;
        }

        public Node(Node<T> parent, T answer)
        {
            this.parent = parent;
            this.answer = answer;
        }

        public Node<T> onYes(Decision nextDecision)
        {
            return (this.yes = new Node<T>(this, nextDecision));
        }

        /* Returns this node, not the leaf! */
        public Node<T> onYes(T answer)
        {
            return (this.yes = new Node<T>(this, answer)).getParent();
        }

        public Node<T> onNo(Decision nextDecision)
        {
            return (this.no = new Node<T>(this, nextDecision));
        }

        /* Returns this node, not the leaf! */
        public Node<T> onNo(T answer)
        {
            return (this.no = new Node<T>(this, answer)).getParent();
        }
        
        public Node<T> setDecision(Decision decision)
        {
            this.decision = decision;
            return this;
        }

        public Decision getDecision()
        {
            return decision;
        }

        public T getAnswer()
        {
            return answer;
        }

        boolean isLeaf()
        {
            return (yes == null && no == null);
        }

        public Node<T> getParent()
        {
            return parent;
        }
    }

    private Node<T> root;

    public BinaryDecisionTree(Node<T> rootNode)
    {
        this.root = rootNode;
    }

    public BinaryDecisionTree()
    {
        this(new Node<T>(null, (Decision)null));
    }

    public Node<T> getRoot()
    {
        return root;
    }

    public T decide()
    {
        Node<T> currentNode = root;
        while (currentNode != null && !currentNode.isLeaf() )
        {
            currentNode = (currentNode.decision.isYes()? currentNode.yes : currentNode.no);
        }
        return (currentNode == null? null : currentNode.answer);
    }

}
