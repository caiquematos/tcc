package com.example.caique.arvore;

import java.util.ArrayList;

/**
 * Created by caique on 31/03/15.
 */
public class Edge {
    public ArrayList<Node> list;
    public Node initial;

    public Edge(Node node){
        list = new ArrayList<Node>();
        this.initial = node;
    }

    public boolean isEmpty(){
        return list.isEmpty();
    }

    public void remove(int index){
        list.remove(index);
    }

    public ArrayList<Node> getList() {
        return list;
    }

    public void insert(Node node){
        this.list.add(node);
    }

    public void setList(ArrayList<Node> list) {
        this.list = list;
    }

    public Node getNode(int index){
        return list.get(index);
    }

    public void update(ArrayList<Node> list){
        this.list = list;
    }
}
