package meta.pattern;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import meta.entity.AttrValEntry;
import meta.entity.AttributeIndexs;
import meta.entity.Pattern;
import weka.core.Instance;
import weka.core.Instances;

public class IgFilter {
	
	
	/**
	 * filter frequent patterns according to threshold
	 * @param inss
	 * @param train
	 * @param pats
	 * @param threshold
	 * @return
	 * @throws IOException
	 */
	public static List<Pattern> filter(Instances inss, Instances train, List<Pattern> pats, int topk) throws IOException {
		/* load instances and patterns */
		//inss = loadDataSet(Constant.DATASET_ARFF);
		//pats = PatternLoader.loadPattern();
		double info = infoOfTrain(train);
		
		List<Pattern> rst = new ArrayList<Pattern>();
		for (Pattern pat : pats) {
			double gain = info - infoOfPattern(train, pat);
			pat.setIg(gain);
			rst.add(pat);
		}
		Collections.sort(rst);
		return rst.subList(0, topk);
	}
	
	
	/*
	 * calculate information gain according to a given pattern
	 * @param train
	 * @param pattern
	 * @return
	 */
	private static double infoOfPattern(Instances train, Pattern pattern) {
		// construct D_fit 
		List<Instance> fitInstances = new ArrayList<Instance>();
		List<Instance> noFitInstances = new ArrayList<Instance>();
		for (int i = 0; i < train.numInstances(); i++) {
			Instance ins = train.instance(i);
			if (isFit(ins, pattern)) {
				fitInstances.add(ins);
			} else {
				noFitInstances.add(ins);
			}
		}
		double total = train.numInstances();
		double fitCount = fitInstances.size();
		
		double info1 = infoOfPartialTrain(fitInstances);
		double info2 = infoOfPartialTrain(noFitInstances);
		
		double ig = fitCount/total*info1 + (total-fitCount)/total*info2;
		
		return ig;
	}
	
	/*
	 * calculate total information gain of training data
	 * @param train
	 * @return
	 */
	private static double infoOfTrain(Instances train) {
		train.setClassIndex(train.numAttributes()-1);
		double total = train.numInstances();
		double count1 = 0;
		for (int i = 0; i < train.numInstances(); i++) {
			Instance ins = train.instance(i);
			if (ins.classValue() == 0.0) {
				count1++;
			}
			//System.out.println(ins.classValue());
		}
		double count2 = total - count1;
		double ig = (-count1/total*Math.log(count1/total)/Math.log(2) - count2/total*Math.log(count2/total)/Math.log(2));
		return ig;
	}
	
	/*
	 * calculate partial information gain of partial training data(fit / no-fit)
	 * @param insList
	 * @return
	 */
	private static double infoOfPartialTrain(List<Instance> insList) {
		double count1 = 0;
		for (int i = 0; i < insList.size(); i++) {
			Instance ins = insList.get(i);
			if (ins.classValue() == 0.0) {
				count1++;
			}
			//System.out.println(ins.classValue());
		}
		double total = insList.size();
		double count2 = total - count1;
		return (-count1/total*Math.log(count1/total)/Math.log(2) - count2/total*Math.log(count2/total)/Math.log(2));
	}
	
	private static boolean isFit(Instance ins, Pattern pattern) {
		for (AttrValEntry entry : pattern.entrys()) {
			int id = AttributeIndexs.get(entry.getAttr());
			if (id < 0) {
				System.out.println("bug");
			}
			String v = ins.stringValue(id);
			if (!v.equals(entry.getVal())) {
				return false;
			}
		}
		return true;
	}
	
	
	
	public static void main(String[] args) {
		
	}
}
