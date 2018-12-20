package com.koishi.mua;

class Facility {
	public Processor processor;
	public ASTBuilder astBuilder;

	Facility() {
		this.processor = new Processor();
		this.astBuilder = new ASTBuilder();
	}
}
