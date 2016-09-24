package main;

import java.util.HashMap;
import java.util.Map;

import com.sale.util.BlowUtils;
import com.sales.constants.BlowParam;
import com.sales.core.BlowCore;
import com.sales.pojo.Prodcty;
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
			BlowUtils.getContext().openSession();
			Map m=new HashMap();
			m.put("product", BlowCore.getInstance().getContext().getBasis(Prodcty.class).propEquals("id", 12).retrieveOne());
			System.out.println(new XStream().toXML(BlowCore.getInstance().getContext().getSQLResult("getAllProducts", null)));
			System.out.println(new XStream().toXML(BlowCore.getInstance().getContext().getSQLResult("getProducts", m)));
			//System.out.println(new XStream().toXML(BlowCore.getInstance().getContext().getBasis(Prodcty.class).retrieveMany(null)));
			BlowUtils.getContext().closeSession();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
