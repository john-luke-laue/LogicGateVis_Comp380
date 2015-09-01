package com.model;

import java.util.ArrayList;


public class InputGate extends Gate {

	private final String type = "input";
	private int output = 1;
	private int input = 1;
	
	
	public String getType() {
		return type;
	}
	
	public int getOutput() {
		return output;
	}
	
	public int getInput() {
		return input;
	}
	
	public void setInput(int i) {
		input = i;
	}
	
	public InputGate(String id, int gid) {
		// TODO Auto-generated constructor stub	
		super(id, gid);
	}
	
}
