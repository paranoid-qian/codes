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
	public static Instances[] genTrainTest(double trainRatio, Instances inss, int numFold) {
		Instances train = null;
		Instances test = null;
		
		if (trainRatio < 0.1 || trainRatio >=1) {
			throw new IllegalArgumentException("Train ratio must be [0.1, 1).");
		}
		
		int numInstances = inss.numInstances();
		int numInstForFold = (int) ( numInstances * trainRatio);
		int offset = (int) (numInstances * 0.1);
		
		if (trainRatio == 0.1) {
			train = inss.testCV(10, numFold);
			test = inss.trainCV(10, numFold);
		} else {
			train = new Instances(inss, numInstForFold);
			test = new Instances(inss, numInstances - numInstForFold);
			
			int trainFrom = numFold * offset;
			cpyInstances(trainFrom, train, numInstForFold, inss);
			if (trainFrom + numInstForFold <= numInstances) {
				copyInstances(0, test, trainFrom, inss);
				copyInstances(trainFrom+numInstForFold, test, numInstances-trainFrom-numInstForFold, inss);
			} else {
				int from = (trainFrom+numInstForFold) % numInstances;
				copyInstances(from, test, numInstances-numInstForFold, inss);
			}
		}
		return new Instances[]{train, test};
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
