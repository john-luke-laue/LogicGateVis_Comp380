package com.model;
import java.util.*;


public abstract class AbstractGraph<V> implements Graph<V> {
  protected ArrayList<Gate> vertices; // Store vertices (gates)
  protected ArrayList<WeightedEdge> edges; //Store edges
  protected List<List<Gate>> neighbors;
  protected ArrayList<ArrayList<Gate>> leveledList;
  int maxLevel = 0;
  
  
  public ArrayList<ArrayList<Gate>> getLeveledList() {
	  return leveledList;
  }
  
  public int getMaxLevel() {
	  return maxLevel;
  }
  
  public ArrayList<Gate> getVertices() {
	return vertices;
  }

  public void setVertices(ArrayList<Gate> vertices) {
	this.vertices = vertices;
  }

  public ArrayList<WeightedEdge> getEdges() {
	return edges;
  }

  public void setEdges(ArrayList<WeightedEdge> edges) {
	this.edges = edges;
  }

  public List<List<Gate>> getNeighbors() {
	return neighbors;
  }

  public void setNeighbors(List<List<Gate>> neighbors) {
	this.neighbors = neighbors;
  }

  protected AbstractGraph(ArrayList<Gate> gates, ArrayList<WeightedEdge> connectors) {
	  this.vertices = new ArrayList<Gate>();
	  this.edges = new ArrayList<WeightedEdge>();
	  
	  //System.out.println("Gates: ");
	  for (Gate g : gates) {
		this.vertices.add(g);  
		//System.out.println(g.toString());
	  } 
	  //System.out.println();
	  //System.out.println("WEdges: ");
	  
	  for (WeightedEdge w : connectors) {
		  this.edges.add(w);
		  //System.out.println(w.toString());
	  }
	  
	  createAdjacencyList(edges, vertices.size());
	  

	  
	  

  }
  
  public int getNumVerts() {
	  return vertices.size();
  }
  
  private void createAdjacencyList(ArrayList<WeightedEdge> edges, int numVertices) {
	  //Create a linked list
	  neighbors = new ArrayList<List<Gate>>();
	  for(int i = 0; i < numVertices; i++) {
		  neighbors.add(new ArrayList<Gate>());
	  }
	  
	  for(WeightedEdge edge : edges) {
		  neighbors.get(edge.u.getGid()).add(edge.v);
	  }
  }
  
  public int[][] getAdjacencyMatrix() {
	  int[][] adjacencyMatrix = new int[getNumVerts()][getNumVerts()];
	  
	  for(int i = 0; i < neighbors.size(); i++) {
		  for(int j = 0; j < neighbors.get(i).size(); j++) {
			  Gate v = neighbors.get(i).get(j);
			  adjacencyMatrix[i][v.getGid()] = 1;
		  }
	  }
	  return adjacencyMatrix;
  }
  
  public void printAdjacencyMatrix() {
	  int[][] adjacencyMatrix = getAdjacencyMatrix();
	  for(int i = 0; i < adjacencyMatrix.length; i++) {
		  for(int j = 0; j < adjacencyMatrix[0].length; j++) {
			  System.out.print(adjacencyMatrix[i][j] + " ");
		  }
		  System.out.println();
	  }
  }
  
  
  
  /////////BFS and Tree
  
  /** Starting bfs search from vertex v */
  /** To be discussed in Section 27.7 */
  public Tree bfs(Gate v) {
    List<Integer> searchOrders = new ArrayList<Integer>();
    Gate[] parent = new Gate[vertices.size()];
    for (int i = 0; i < parent.length; i++)
      parent[i] = new Gate("-1", -1); // Initialize parent[i] to -1

    java.util.LinkedList<Gate> queue = new java.util.LinkedList<Gate>(); // list used as a queue
    boolean[] isVisited = new boolean[vertices.size()];
    queue.offer(v); // Enqueue v
    isVisited[v.getGid()] = true; // Mark it visited

    
    System.out.println("BFS: ");
    int i = 0;
    while (!queue.isEmpty()) {
      Gate u = queue.poll(); // Dequeue to u
      searchOrders.add(u.getGid()); // u searched
      for (Gate w : neighbors.get(u.getGid())) {
        if (!isVisited[w.getGid()]) {
          queue.offer(w); // Enqueue w
          parent[w.getGid()] = u; // The parent of w is u
          isVisited[w.getGid()] = true; // Mark it visited
        }
      }
      i++;
    }

    return new Tree(v, parent, searchOrders);
  }

  /** Tree inner class inside the AbstractGraph class */
  /** To be discussed in Section 27.5 */
  public class Tree {
    private Gate root; // The root of the tree
    private Gate[] parent; // Store the parent of each vertex
    private List<Integer> searchOrders; // Store the search order

    /** Construct a tree with root, parent, and searchOrder */
    public Tree(Gate root, Gate[] parent, List<Integer> searchOrders) {
      this.root = root;
      this.parent = parent;
      this.searchOrders = searchOrders;
    }

    /** Construct a tree with root and parent without a
     *  particular order */
    public Tree(Gate root, Gate[] parent) {
      this.root = root;
      this.parent = parent;
    }

    /** Return the root of the tree */
    public Gate getRoot() {
      return root;
    }

    /** Return the parent of vertex v */
    public Gate getParent(Gate v) {
      return parent[v.getGid()];
    }

    /** Return an array representing search order */
    public List<Integer> getSearchOrders() {
      return searchOrders;
    }

    /** Return number of vertices found */
    public int getNumberOfVerticesFound() {
      return searchOrders.size();
    }
    
    /** Return the path of vertices from a vertex index to the root */
    public List<Gate> getPath(Gate index) {
      ArrayList<Gate> path = new ArrayList<Gate>();

      do {
        path.add(vertices.get(index.getGid()));
        index = parent[index.getGid()];
      }
      while (index.getGid() != -1);

      return path;
    }

    /** Print a path from the root to vertex v */
    public void printPath(Gate index) {
      List<Gate> path = getPath(index);
      System.out.print("A path from " + vertices.get(root.getGid()) + " to " +
        vertices.get(index.getGid()) + ": ");
      for (int i = path.size() - 1; i >= 0; i--)
        System.out.print(path.get(i) + " ");
    }

    /** Print the whole tree */
    public void printTree() {
      System.out.println("Root is: " + vertices.get(root.getGid()));
      System.out.print("Edges: ");
      for (int i = 0; i < parent.length; i++) {
        if (parent[i].getGid() != -1) {
          // Display an edge
          System.out.print("(" + vertices.get(parent[i].getGid()) + ", " +
            vertices.get(i) + ") ");
        }
        System.out.println("");
      }
      System.out.println("");
    }
  }
  
  
  

//BellmanFord = Longest Path
  public void BellmanFord() {
  // This implementation takes in a graph, represented as lists of vertices
  // and edges, and modifies the vertices so that their distance and
  // predecessor attributes store the shortest paths.
	  ArrayList<Gate> vertices = this.vertices;
	  ArrayList<WeightedEdge> edges = this.edges;
	  Gate source = this.vertices.get(0);
	  
	  maxLevel = 0;
	  
	  // Step 1: initialize graph
	  for (Gate v : vertices) {
		  if(v.getGid() == source.getGid()) {
			  v.setDist(0);
		  }
		  else {
			  v.setDist(999999);  //infinity
		  }
		  v.setPred(null);
	  }
	  
	  // Step 2: relax edges repeatedly
	  for(int i = 1; i <= vertices.size()-1; i++) {
		  for (WeightedEdge e : edges) {
			  Gate u = e.getU();
			  Gate v = e.getV();
			  if ( (u.getDist() + e.getWeight()) < v.getDist()) {
				  v.setDist(u.getDist() + e.getWeight());
				  v.setPred(u);
			  }
		  }
	  }
	  
	  // Step 3: check for negative-weight cycles
	  for (WeightedEdge e : edges) {
		  Gate u = e.getU();
		  Gate v = e.getV();
		  if(u.getDist() + e.getWeight() < v.getDist()) {
			  System.out.println("BellmanFord_Error: Graph contains a negative-weight cycle!");
		  }
	  }
	  
	  // Step 4: change all Gate.Dist's to abs value
	  for (Gate v : vertices) {
		  v.setDist(Math.abs(v.getDist()));
		  if(v.getDist() > maxLevel)
			  maxLevel = v.getDist();
	  }
  }
  
  public void createLeveledList()
  {   
	  leveledList = new ArrayList<ArrayList<Gate>>();
	  int i = 0;
	  while (i <= this.maxLevel) {
		  leveledList.add(new ArrayList<Gate>());
		  for (Gate g : this.vertices) {
			  if(g.getDist() == i)
			  {
				  //System.out.println(g.getDist());
				  leveledList.get(i).add(g);
			  }
		  }
		  i++;
	  }
	  
	  //printLeveledList();
  }
  
  public void printLeveledList()
  {
	  System.out.println("Printing leveled list ...");
	  
	  for(int k = 0; k < leveledList.size(); k++)
	  {
		  for(int j = 0; j < leveledList.get(k).size(); j++)
		  {
			  System.out.print(leveledList.get(k).get(j).getId() + " ");
			  System.out.println(leveledList.get(k).get(j).getDist());
		  }
	  }
  }


}
