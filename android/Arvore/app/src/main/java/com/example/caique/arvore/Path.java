package com.example.caique.arvore;

import java.util.ArrayList;

/**
 * Created by caique on 31/03/15.
 */
public class Path {
    private ArrayList<Node> path;

    public void insert(Node node){
        path.add(node);
    }

    public ArrayList<Node> getPath() {
        return path;
    }

    public void setPath(ArrayList<Node> path) {
        this.path = path;
    }
}
