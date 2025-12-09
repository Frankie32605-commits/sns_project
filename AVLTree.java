import java.util.ArrayList;
import java.util.List;

public class AVLTree<T extends Comparable<T>> {
    // AVL Tree implementation details would go here
    private Node root;

    private class Node{
        T data;
        Node left, right;
        int height = 1;

        Node(T data){
            this.data = data;
        }
    }

    public void insert(T data){
        root = insert(root, data);
    }

    private Node insert(Node node, T data){
        if(node == null) return new Node(data);

        if(data.compareTo(node.data) < 0)
            node.left = insert(node.left, data);
        else if(data.compareTo(node.data) > 0)
            node.right = insert(node.right, data);
        else
            return node;
        
        node.height = 1 + Math.max(height(node.left), height(node.right));
        return balance(node, data);
    }

    private Node balance(Node node, T data){
        int balance = getBalance(node);

        //Focuses on the Left
        if(balance > 1){
            if(data.compareTo(node.left.data) < 0)
                return rightRotate(node);
            else{
                node.left = leftRotate(node.left);
                return rightRotate(node);
            }
        }

        //Focuses on the Right
        if(balance < -1){
            if(data.compareTo(node.right.data) > 0)
                return leftRotate(node);
            else{
                node.right = rightRotate(node.right);
                return leftRotate(node);
            }
        }
        return node;
    }

    private Node rightRotate(Node y){
        Node x = y.left;
        Node T = x.right;
        x.right = y;
        y.left = T;
        y.height = 1 + Math.max(height(y.left), height(y.right));
        x.height = 1 + Math.max(height(x.left), height(x.right));
        return x;
    }

    private Node leftRotate(Node x){
        Node y = x.right;
        Node T = y.left;
        y.left = x;
        x.right = T;
        x.height = 1 + Math.max(height(x.left), height(x.right));
        y.height = 1 + Math.max(height(y.left), height(y.right));
        return y;
    }

    private int height(Node n) { 
        return n == null ? 0 : n.height; 
    }
    private int getBalance(Node n) { 
        return n == null ? 0 : height(n.left) - height(n.right); 
    }
        
    public List<T> inOrderTraversal() {
        List<T> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }
}