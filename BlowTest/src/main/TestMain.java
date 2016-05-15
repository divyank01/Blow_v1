package main;

import com.sales.core.BlowCore;
import com.thoughtworks.xstream.XStream;



public class TestMain {

	public static void main(String[] args) {
		/*ConnPool pool=ConnPool.getPool();
		System.out.println(pool.loan());
		Connection t=pool.loan();
		System.out.println(t);
		pool.submit(t);
		System.out.println(pool.loan());
		System.out.println(pool.loan());*/
		
		try {
			//Class.forName(ORM_QUERY_Parser.class.getCanonicalName());
			//System.out.println(new XStream().toXML(ORM_QUERY_Parser.getInstance()));
			System.out.println(new XStream().toXML(BlowCore.getInstance().getContext().getSQLResult("getAllProducts", null)));
			System.out.println(new XStream().toXML(BlowCore.getInstance().getContext().getSQLResult("getProducts", null)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
