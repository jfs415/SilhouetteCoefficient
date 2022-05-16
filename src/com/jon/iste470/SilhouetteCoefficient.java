package com.jon.iste470;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SilhouetteCoefficient {

	private static final File LIB = new File("lib");
	private static HashMap<String, LinkedHashSet<DataInstance>> clusterMap = new HashMap<>(); //Map data instance to each cluster. Can get clusters from clusterMap::keySet
	private static LinkedHashSet<DataInstance> instances = new LinkedHashSet<>();

	private static void printDistances() {
		DataInstance prev = null;
		for (String cluster : clusterMap.keySet()) {
			System.out.println("Processing data instances for " + cluster);
			for (DataInstance instance : clusterMap.get(cluster)) {
				if (prev != null) {
					System.out.println("Distance from: " + prev.getInstance() + " -> " + instance.getInstance() + " " + euclideanDistance(prev, instance));
				}

				prev = instance;
			}
			System.out.println("\n");
		}
	}

	private static double euclideanDistance(DataInstance p1, DataInstance p2) {
		double sepalLength = Math.pow((p1.getSepalLength() - p2.getSepalLength()), 2);
		double sepalWidth = Math.pow((p1.getSepalWidth() - p2.getSepalWidth()), 2);
		double petalLength = Math.pow((p1.getPetalLength() - p2.getPetalLength()), 2);
		double petalWidth = Math.pow((p1.getPetalWidth() - p2.getPetalWidth()), 2);

		return Math.sqrt((sepalLength + sepalWidth + petalLength + petalWidth));
	}

	private static void cohesion() {
		for (Map.Entry<String, LinkedHashSet<DataInstance>> entry : clusterMap.entrySet()) { //Iterate through each entry in clusterMap
			for (DataInstance instance : entry.getValue()) {
				double difference = 0.0;

				//Create set without the data instance above
				Set<DataInstance> otherInstances = entry.getValue().stream().filter(value -> value.getInstance() != instance.getInstance()).collect(Collectors.toSet());

				for (DataInstance otherInstance : otherInstances) {
					difference += euclideanDistance(instance, otherInstance);
				}

				double cohesion = (difference / otherInstances.size());
				instance.setAi(cohesion);
			}
		}

	}

	private static void separation() {
		ArrayList<Double> separations = new ArrayList<>();

		for (DataInstance instance : instances) {
			for (Map.Entry<String, LinkedHashSet<DataInstance>> entry : clusterMap.entrySet()) {
				if (!entry.getKey().equalsIgnoreCase(instance.getCluster())) { //Check to make sure not the same cluster
					double difference = 0.0;

					for (DataInstance entryInstance : entry.getValue()) {
						difference += euclideanDistance(instance, entryInstance);
					}

					separations.add(difference / entry.getValue().size());
				}
			}

			//Calculate lowest to be set as bi value
			double lowest = Double.MAX_VALUE;
			for (double value : separations) {
				lowest = Math.min(lowest, value);
			}
			instance.setBi(lowest);
			separations.clear(); //Clear before starting next cluster
		}

	}

	private static void silhouetteCoefficient() {
		for (DataInstance instance : instances) {
			instance.setSi(1 - (instance.getAi() / instance.getBi()));
		}
	}

	/**
	 * Parse the comma separated values in each line.
	 * Creates a new DataInstance Object from the parsed values.
	 *
	 * @param line
	 * 			  Data line from ARFF file
	 */

	private static void processLine(String line) {
		String[] lineData = line.split(",");
		int instance = Integer.parseInt(lineData[0]);
		double sepLength = Double.parseDouble(lineData[1]);
		double sepWidth = Double.parseDouble(lineData[2]);
		double petLength = Double.parseDouble(lineData[3]);
		double petWidth = Double.parseDouble(lineData[4]);
		String cluster = lineData[5];

		DataInstance dataInstance = new DataInstance(cluster, instance, sepLength, sepWidth, petLength, petWidth);
		instances.add(dataInstance);
		clusterMap.computeIfAbsent(cluster, v -> new LinkedHashSet<>()).add(dataInstance);
	}

	private static void readFile(File arffFile) {
		boolean isData = false;

		try (BufferedReader br = new BufferedReader(new FileReader(arffFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("@data")) {
					isData = true;
				} else if (isData) {
					processLine(line);
				}
			}

			//printDistances(); //Uncomment to print the euclidean distances for each point

			//Set the ai, bi and si values for each data instance
			cohesion();
			separation();
			silhouetteCoefficient();

			//Uncomment to see the ai, bi and si for each data point instance
			//instances.forEach(System.out::println);

			for (Map.Entry<String, LinkedHashSet<DataInstance>> entry : clusterMap.entrySet()) {
				double clusterAverage = 0.0;

				for (DataInstance instance : entry.getValue()) {
					clusterAverage += instance.getSi();
				}

				System.out.println("The Average Silhouette Coefficient for " + entry.getKey() + " is: " + (clusterAverage / entry.getValue().size()));
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally { //Clear before reading another file
			clusterMap.clear();
			instances.clear();
		}
	}

	public static void main(String... args) {
		try {
			if (!LIB.exists()) {
				System.out.println("Missing library folder with ARFF files!");
				System.exit(0);
			}

			for (File file : Objects.requireNonNull(LIB.listFiles(), "Unable to find arff files to process!")) {
				System.out.println("Processing File: " + file.getName());
				readFile(file);
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
