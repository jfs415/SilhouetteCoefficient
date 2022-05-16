package com.jon.iste470;

public class DataInstance {

	private final String cluster;
	private final int instance;
	private final double sepalWidth;
	private final double petalWidth;
	private final double sepalLength;
	private final double petalLength;
	private double ai;
	private double bi;
	private double si;

	public DataInstance(String cluster, int instance, double sepalLength, double sepalWidth, double petalLength, double petalWidth) {
		this.cluster = cluster;
		this.instance = instance;
		this.sepalLength = sepalLength;
		this.sepalWidth = sepalWidth;
		this.petalLength = petalLength;
		this.petalWidth = petalWidth;
		this.ai = 0.0;
		this.bi = 0.0;
		this.si = 0.0;
	}

	public String getCluster() {
		return cluster;
	}

	public int getInstance() {
		return instance;
	}

	public double getSepalWidth() {
		return sepalWidth;
	}

	public double getPetalWidth() {
		return petalWidth;
	}

	public double getSepalLength() {
		return sepalLength;
	}

	public double getPetalLength() {
		return petalLength;
	}

	public double getAi() {
		return ai;
	}

	public void setAi(Double ai) {
		this.ai = ai;
	}

	public double getBi() {
		return bi;
	}

	public void setBi(Double bi) {
		this.bi = bi;
	}

	public double getSi() {
		return si;
	}

	public void setSi(Double si) {
		this.si = si;
	}

	@Override
	public String toString() {
		return cluster + " Instance " + instance + "\nAI: " + ai + "\nBI: " + bi + "\nSI: " + si + "\n";
	}

}
