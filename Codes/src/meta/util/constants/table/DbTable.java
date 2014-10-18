package meta.util.constants.table;

/**
 * DbTable interface
 * @author paranoid
 *
 */
public interface DbTable {
	
	/* table columns */
	public String[] columns();
	
	/* table name */
	public String tableName();
	
	/* table class name */
	public String className();
}
