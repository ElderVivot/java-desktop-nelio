package db;

public class DBIntegratyExpection extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public DBIntegratyExpection(String msg) {
		super(msg);
	}

}
