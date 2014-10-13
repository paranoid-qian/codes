package meta.tableconstants;

public interface BreastCancerTable {
	
	public static final String table_name = "breast_cancer";
	
	public static final String col_id = "id";
	public static final String col_age = "age";
	public static final String col_menopause = "menopause";
	public static final String col_tumor_size = "tumor_size";
	public static final String col_inv_nodes = "inv_nodes";
	public static final String col_node_caps= "node_caps";
	public static final String col_deg_malig = "deg_malig";
	public static final String col_breast = "breast";
	public static final String col_breast_quad = "breast_quad";
	public static final String col_irradiat = "irradiat";
	public static final String col_class = "class";
	
	
	public static final String[] columns = {col_age, col_menopause, col_tumor_size, col_inv_nodes, col_node_caps, col_deg_malig, col_breast, col_breast_quad, col_irradiat};
}
