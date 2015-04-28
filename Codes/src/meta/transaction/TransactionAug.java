package meta.transaction;

import java.io.IOException;
import java.util.List;

import meta.entity.Item;
import meta.entity.Pattern;
import meta.util.constants.Constant;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class TransactionAug {
	
	private static FastVector fv = new FastVector();
	static {
		fv.addElement(Constant.FIT);
		fv.addElement(Constant.NO_FIT);
	}
	
	/**
	 * ��չpattern��instances��
	 * @param patterns
	 * @param instances
	 * @return
	 * @throws IOException 
	 */
	public static Instances augmentDataset(List<? extends Pattern> patterns, Instances instances) throws IOException {
		Instances copy = new Instances(instances);
		
		int newAttrStart = copy.numAttributes() - 1;
		
		// ������attributes����pattern��
		for (Pattern pattern : patterns) {
			copy.insertAttributeAt(new Attribute(pattern.pName(), fv), copy.numAttributes()-1);
		}
		// �����µ�attributes values
		for (int i = 0; i < copy.numInstances(); i++) {
			
			Instance ins = copy.instance(i);
			int index = newAttrStart;
			for (Pattern pattern : patterns) {
				String isFit = pattern.pValue(ins);
				ins.setValue(index++, isFit);
			}
		}
		return copy;
	}
	
	
	/**
	 * �Ľ�V1���augment������
	 * 		1. ���ڽ�class attribute��Ϊ���һ��attribute��ֱ����ԭ�����ݼ���׷��attribute
	 * 		2. out loop - patterns, inner loop - instances
	 * @param patterns
	 * @param instances
	 * @return
	 * @throws IOException
	 */
	public static Instances augmentDatasetV2(List<? extends Pattern> patterns, Instances instances) throws IOException {
		Instances copy = new Instances(instances);
		int numAttributes = copy.numAttributes();
		int numInstances = copy.numInstances();
		for (Pattern pattern : patterns) {
			copy.insertAttributeAt(new Attribute(pattern.pName(), fv), numAttributes);
			for (int i = 0; i < numInstances; i++) {
				Instance ins = copy.instance(i);
				ins.setValue(numAttributes, pattern.pValue(ins));
			}
			numAttributes++;
		}
		return copy;
	}
	
	/**
	 * ����item����ԭ�е����ݼ���used for comparison: single features
	 * @param items
	 * @param instances
	 * @return
	 */
	public static Instances augmentDataset4SingleFeatures(List<Item> items, Instances instances) throws IOException {
		Instances copy = new Instances(instances);
		int numAttributes = copy.numAttributes();
		int numInstances = copy.numInstances();
		for (Item item : items) {
			copy.insertAttributeAt(new Attribute(item.toString(), fv), numAttributes);
			for (int i = 0; i < numInstances; i++) {
				Instance ins = copy.instance(i);
				ins.setValue(numAttributes, item.iValue(ins));
			}
			numAttributes++;
		}
		return copy;
	}
	
}
