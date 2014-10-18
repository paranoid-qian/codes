package meta.util.constants.table;


public class BreastCancerTable implements DbTable{
	
	private static final String table_name = "breast_cancer";
	
	private static final String col_age = "age";
	private static final String col_menopause = "menopause";
	private static final String col_tumor_size = "tumor_size";
	private static final String col_inv_nodes = "inv_nodes";
	private static final String col_node_caps= "node_caps";
	private static final String col_deg_malig = "deg_malig";
	private static final String col_breast = "breast";
	private static final String col_breast_quad = "breast_quad";
	private static final String col_irradiat = "irradiat";
	private static final String col_class = "class";
	
	private static final String[] columns = {col_age, col_menopause, col_tumor_size, col_inv_nodes, col_node_caps, col_deg_malig, col_breast, col_breast_quad, col_irradiat};
	
	@Override
	public String[] columns() {
		return columns;
	}
	
	@Override
	public String tableName() {
		return table_name;
	}
	
	@Override 
	public String className() {
		return col_class;
	}
	
}
