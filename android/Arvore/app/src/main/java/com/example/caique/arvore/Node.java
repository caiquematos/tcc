package com.example.caique.arvore;

/**
 * Created by caique on 31/03/15.
 */
public class Node {
    private int cost;
    private Node father;
    private int depth;
    private char state;

    public Node(Node father, char state, int depth){
        setFather(father);
        setState(state);
        setDepth(depth);
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Node getFather() {
        return father;
    }

    public void setFather(Node father) {
        this.father = father;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public char getState() {
        return state;
    }

    public void setState(char state) {
        this.state = state;
    }
}
