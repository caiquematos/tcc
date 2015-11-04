package com.example.caique.arvore;

import java.util.ArrayList;

/**
 * Created by caique on 31/03/15.
 */
public class Funtion {
    private Edge borda;
    private Node target;
    private Path path;
    private int index;

    public Funtion(Edge borda, Node target){
        this.borda = borda;
        this.target = target;
        index=0;
        path = new Path();
    }

    public boolean isTarget(Node node){
        return (node == target);
    }

    public Path buscaExtension(){
        if (borda.isEmpty()){
            return path;
        }else{
            Node node = borda.getNode(index);
            if( isTarget(node)){
                path.insert(node);
                return path;
            }else{
                path.insert(node);
                borda.remove(index);
                index ++;
                return path;
            }
        }
    }

    public ArrayList<Node> extend(Node node){
        ArrayList<Node> extension = new ArrayList<Node>();

        switch(node.getState()){
            case 'A':
                Node b = new Node(node, 'B', 2);
                Node c = new Node(node, 'C', 2);
                Node h1 = new Node(node, 'H', 2);
                extension.add(b);
                extension.add(c);
                extension.add(h1);
                break;
            case 'B':
                Node d = new Node(node, 'D', 3);
                Node e = new Node(node, 'E', 3);
                extension.add(d);
                extension.add(e);
                break;
            case 'C':
                Node f = new Node(node, 'F', 3);
                Node g = new Node(node, 'G', 3);
                extension.add(f);
                extension.add(g);
                break;
            case 'D':
                Node h2 = new Node(node, 'H', 4);
                extension.add(h2);
                break;
            case 'E':
                break;
            case 'F':
                break;
            case 'G':
                break;
            case 'H':
                break;
        }

        return extension;
    }

}
