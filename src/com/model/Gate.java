package com.model;

import java.util.ArrayList;

public class Gate {
	private String id;
	private int gid;
	private String size;
	private int tempr; //Temperature
	private int flipCount = 0;
	private String color = "white";
	private int dist;  //Distance from root
	private Gate pred; //Predecessor
	
	private ArrayList<Gate> connectedGates = new ArrayList<Gate>(); 
	private String type = "";
	private int prevOutput = -1;
	private int output = -1;
	private ArrayList<Integer> inputList = new ArrayList<Integer>();
	
	public void addToInputList(int inputNum) {
		this.inputList.add(inputNum);
	}
	
	public ArrayList<Integer> getInputList() {
		return inputList;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public int getOutput() {
		return output;
	}
	
	public void setInputGateOutput(int output) {
		this.output = output;
	}
	
	
	public ArrayList<Gate> getConnectedGates() {
		return connectedGates;
	}
	
	public void addConnectedGates(Gate i) {
		connectedGates.add(i);
	}
	
	
	public Gate(String id, int gid){
		this.id = id;
		this.gid = gid;	
	}
	
	public int getGid() {
		return gid;
	}
	public void setGid(int gid) {
		this.gid = gid;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	
	public int getTempr() {
		return tempr;
	}
	public void setTempr(int tempr) {
		this.tempr = tempr;
	}
	
	public int getFlipCount() {
		return flipCount;
	}
	public void setFlipCount(int flipCount) {
		this.flipCount = flipCount;
	}
	
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	
	public int getDist() {
		return dist;
	}
	public void setDist(int dist) {
		this.dist = dist;
	}
	
	public Gate getPred() {
		return pred;
	}
	public void setPred(Gate pred) {
		this.pred = pred;
	}
	
	public String toString() {
		return "gate: " + id + "/" + gid;  
	}
	
	
	
	public void calcOutput() {

		if(type.equals("and")) {	
			for(int i=0; i<inputList.size(); i++) {	
				if(inputList.get(i) == 1) {	
					this.output = 1;	
				}					
				else {						
					this.output = 0;						
					break;				
				}				
			}				
		}
		else if(type.equals("or")) {
			for(int i=0; i<inputList.size(); i++) {
				if(inputList.get(i) == 0)	{
					output = 0;
				}
				else {
					output = 1;
					break;
				}
			}
		}
		else if(type.equals("nand")) {
			for(int i=0; i<inputList.size(); i++) {
				if(inputList.get(i) == 1)	{
					output = 0;
				}
				else {
					output = 1;
					break;
				}
			}
		}
		else if(type.equals("nor")) {
			for(int i=0; i<inputList.size(); i++) {
				if(inputList.get(i) == 0)	{
					output = 1;
				}
				else {
					output = 0;
					break;
				}
			}
		}
		else if(type.equals("xor")) {
			int ones = 0, zeros = 0;
			for(int i=0; i<inputList.size(); i++) {
				if(inputList.get(i) == 1)
					ones++;
				else
					zeros++;
			}
			if(ones == 0 || zeros == 0)
				output = 0;
			else
				output = 1;
		}
		else if(type.equals("xnor")) {
			int ones = 0, zeros = 0;
			for(int i=0; i<inputList.size(); i++) {
				if(inputList.get(i) == 1)
					ones++;
				else
					zeros++;
			}
			if(ones == 0 || zeros == 0)
				output = 1;
			else
				output = 0;
		}
		else if(type.equals("not")) {
			for(int i=0; i<inputList.size(); i++) {
				if(inputList.get(i) == 0)
					output = 1;
				else 
					output = 0;
			}
		}
		else { //type = input
			output = inputList.get(0);
		}
			
	}

	public void clearInputList() {
		if(!type.equals("input") && !(type.equals("dummy"))) {
			inputList.clear();
		}
	}
	
	public void updateFlipCount() {
		if(prevOutput == -1 || prevOutput != output) {
			flipCount++;  
		}
		prevOutput = output;
	}
	
	public void updateColor() {
		if(flipCount >= 3 && flipCount <= 5)
			color = "yellow";
		else if(flipCount > 5) {
			color = "red";
		}
	}
		
	
}
