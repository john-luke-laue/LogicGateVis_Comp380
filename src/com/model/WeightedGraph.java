package com.model;

import java.util.*;


public class WeightedGraph<V> extends AbstractGraph<V> {
	private ArrayList<Gate> connectedGates;
	
  public WeightedGraph(ArrayList<Gate> gates, ArrayList<WeightedEdge> edges) {
	super(gates, edges);
  }
  
  public ArrayList<Gate> getListOfConnectedGates() {
	  return connectedGates;
  }

@Override
public void getVerticiesList() {
	// TODO Auto-generated method stub
	
}

@Override
public void getVertex() {
	// TODO Auto-generated method stub
	
}

@Override
public void getEdge() {
	// TODO Auto-generated method stub
	
}



}