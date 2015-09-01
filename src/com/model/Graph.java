package com.model;

public interface Graph<V> {
	
	/** Critical methods used to interface with Graph */
	public int getNumVerts();
	
	public void BellmanFord();
	
	public void createLeveledList();

	public void printAdjacencyMatrix();
	
	public void getVerticiesList();
	
	public void getVertex();
	
	public void getEdge();
	
	public String toString();

}
