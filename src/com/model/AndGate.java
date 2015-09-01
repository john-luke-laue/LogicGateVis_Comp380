package com.model;

import java.util.*;

public class AndGate extends Gate {
	private final String type = "and";
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
	
	public int getOutput() {
		return output;
	}
	
	
	public AndGate(String id, int gid) {
		// TODO Auto-generated constructor stub	
		super(id, gid);
	}
	
	
	
	
	public void calcOutput() {
		for(int i=0; i<inputList.size(); i++) {
			if(inputList.get(i) == 1)	{
				i++;
				output = 1;
			}
			else {
				output = 0;
				break;
			}
		}		
	}
	
	

}
