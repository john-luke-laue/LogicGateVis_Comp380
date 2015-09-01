package com.model;

public class WeightedEdge {
    public Gate u; // Source
    public Gate v; // Destination
    public int weight; // Weight

    /** Construct an WeightedEdge for (u, v, weight) */
    public WeightedEdge(Gate u, Gate v, int weight) {
      this.u = u;
      this.v = v;
      this.weight = weight;
    }
    
    
    public Gate getU() {
		return u;
	}

	public void setU(Gate u) {
		this.u = u;
	}

	public Gate getV() {
		return v;
	}

	public void setV(Gate v) {
		this.v = v;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}


	public String toString() {
    	return "(" + u.getId() + "/"+ u.getGid() + "-" + v.getId() + "/"+ v.getGid() + ")";
    }
}
 

