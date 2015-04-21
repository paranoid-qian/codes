package meta.gen;

import weka.core.Instances;

public class TrainTestGen {
	
	
	/**
	 * gen train for 10-fold validation
	 * @param trainRatio
	 * @param inss
	 * @param numFold: 0-9
	 * @return
	 */
	public static Instances genTrain(double trainRatio, Instances inss, int numFold) {
		Instances train = null;
		
		if (trainRatio < 0.1 || trainRatio >=1) {
			throw new IllegalArgumentException("Train ratio must be [0.1, 1).");
		}
		int numInstForFold = (int) (inss.numInstances() * trainRatio);
		int offset = (int) (inss.numInstances() * 0.1);
		if (trainRatio == 0.1) {
			train = inss.testCV(10, numFold);
		} else {
			int from = numFold * offset;
			train = new Instances(inss, numInstForFold);
			cpyInstances(from, train, numInstForFold, inss);
		}
		return train;
	}
	
	public static Instances genTest(double trainRatio, Instances inss, int numFold) {
		Instances test = null;
		if (trainRatio < 0.1 || trainRatio >=1) {
			throw new IllegalArgumentException("Train ratio must be [0.1, 1).");
		}
		int numInstForFold = (int) (inss.numInstances() * trainRatio);
		int offset = (int) (inss.numInstances() * 0.1);
		if (trainRatio == 0.1) {
			test = inss.trainCV(10, numFold);
		} else {
			test = new Instances(inss, inss.numInstances() - numInstForFold);
			int trainFrom = numFold * offset;
			if (trainFrom + numInstForFold <= inss.numInstances()) {
				copyInstances(0, test, trainFrom, inss);
				copyInstances(trainFrom+numInstForFold, test, inss.numInstances()-trainFrom-numInstForFold, inss);
			} else {
				int from = (trainFrom+numInstForFold)%inss.numInstances();
				copyInstances(from, test, inss.numInstances()-numInstForFold, inss);
			}
		}
		return test;
	}
	
	private static void cpyInstances(int from, Instances dest, int num, Instances inss) {
		int numInss = inss.numInstances();
		if (from + num <= numInss) {
			copyInstances(from, dest, num, inss);
		}
		else {
			copyInstances(from, dest, numInss-from, inss);
			copyInstances(0, dest, (from+num)%numInss, inss);
		}
	}
	
	private static void copyInstances(int from, Instances dest, int num, Instances inss) {
		for (int i = 0; i < num; i++) {
			dest.add(inss.instance((from + i)));
		}
	}
}
