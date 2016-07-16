package main;

import java.sql.SQLData;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.school.Student;

import com.customer.pojo.Customer;
import com.sale.util.BlowUtils;
import com.sales.constants.BlowParam;
import com.sales.constants.SQLTypes;
import com.sales.core.BlowContextImpl;
import com.sales.core.BlowCore;
import com.sales.pojo.ElectronicProductDetails;
import com.sales.pojo.Prodcty;
import com.sales.pojo.ProductDetails;
import com.sales.pojo.Stock;
import com.sales.pojo.StockMappings;
import com.sales.pools.ConnectionPool;
import com.sales.pools.OrmMappingPool;
import com.thoughtworks.xstream.XStream;

public class MainClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Date d=new Date();
			long l=d.getTime();
			
			/*BlowUtils.getContext().openSession();
			Object p=(Object) BlowUtils
					.getBasis(Prodcty.class)
					//.prop(BlowParam.GT, "id", 0).prop(BlowParam.GT, "epd.id", 0)
					//.prop(BlowParam.LIKE_AROUND, "name", "")
					.retrieveMany(null);

			Object p1=(Object) BlowUtils
					.getBasis(Prodcty.class)
					.propEquals("id",121)
					.retrieveOne();

			Object p2=(Object) BlowUtils
					.getBasis(Prodcty.class)
					.propEquals("id",1)
					.retrieveOne();

			Object p3=(Object) BlowUtils
					.getBasis(Prodcty.class)
					.propEquals("id",12)
					.retrieveOne();
			
			//.retrieveMany(null);

			//Map m= new HashMap();
			//m.put("product", p);
			//System.out.println(new XStream().toXML());
			//BlowCore.getInstance().getContext().getSQLResult("getProducts", m);
			BlowUtils.getContext().closeSession();
			/*p.setId(126);
			p.getEpd().setId(125);
			p.getEpd().setProdId(124);*/

			//BlowUtils.getContext().saveOrUpdateEntity(p);
			//System.out.println(new XStream().toXML(p));


			/*ProductDetails p1=(ProductDetails) BlowUtils
			.getBasis(ProductDetails.class)
			.propEquals("id",254)
			.retrieveOne();
			System.out.println(new XStream().toXML(p1));


			ElectronicProductDetails p2=(ElectronicProductDetails) BlowUtils
			.getBasis(ElectronicProductDetails.class)
			.propEquals("product.details.id",254)
			.retrieveOne();
			System.out.println(new XStream().toXML(p2));*/




			/*Object p3= BlowUtils
							.getBasis(Prodcty.class)
							//.propEquals("stock.mappings.locId","10")
							.propEquals("id", 121)
							//.propEquals("catId", 2)
							.retrieveOne();*/

			/*Object p4= BlowUtils
					.getBasis(Customer.class)
					//.propEquals("stock.mappings.locId","10")
					.propEquals("id", 1234)
					//.propEquals("catId", 2)
					.retrieveOne();

			//System.out.println(new XStream().toXML(p3));
			System.out.println(new XStream().toXML(p4));*/


			BlowUtils.getContext().openSession();
			Object o=BlowUtils
			.getBasis(Prodcty.class)
			.propEquals("id",1008)
			.retrieveOne();
			Prodcty p=(Prodcty)o;
			//p.setId(10230);
			BlowUtils.getContext().saveOrUpdateEntity(o);
			Map m= new HashMap();
			m.put("product", o);
			//System.out.println(new XStream().toXML(BlowUtils.getContext().getSQLResult("getProducts", m)));
			System.out.println(new XStream().toXML(o));
			for(int i=0;i<10;i++){
				Student s= new Student();
				//s.setId(i);
				s.setFirstName("fhg"+i);
				s.setLastName("ghdhh"+i);
				s.setAge(i);
				//System.out.println(new XStream().toXML(BlowUtils.getContext().getSQLResult("getProducts", m)));
				//BlowUtils.getContext().saveOrUpdateEntity(s);
			}
			//BlowUtils.getContext().rollback();
			Prodcty p3=new Prodcty();
			List list = new ArrayList();
			//for(int i=0;i<1000;i++){
			Prodcty p4=new Prodcty();
			p3=p4;
			p4.setCatId(2000);
			//p4.setId(1002);
			p4.setName("testy");
			ElectronicProductDetails epd=new ElectronicProductDetails();
			Stock s= new Stock();
			Stock s1= new Stock();
			Stock s2= new Stock();
			StockMappings mappi=new StockMappings();
			StockMappings mappi1=new StockMappings();
			StockMappings mappi2=new StockMappings();
			ProductDetails det=new ProductDetails();

			s1.setMappings(mappi1);
			mappi1.setGeneratedProductId(2347);
			//mappi1.setId(123);
			mappi1.setLocId(121);
			mappi1.setStock(s1);
			//mappi1.setStockId(124);
			//s1.setId(124);
			s1.setLiveStockCount(3724);
			s1.setProduct(p4);
			//s1.setProductId(122);

			s2.setMappings(mappi2);
			mappi2.setGeneratedProductId(2347);
			//mappi2.setId(124);
			mappi2.setLocId(121);
			mappi2.setStock(s);
			//mappi2.setStockId(125);
			//s2.setId(125);
			s2.setLiveStockCount(3724);
			s2.setProduct(p4);
			//s2.setProductId(122);

			List<Stock> stocks=new ArrayList<Stock>();
			stocks.add(s);
			stocks.add(s1);
			stocks.add(s2);
			p4.setEpd(epd);
			p4.setDetails(det);
			p4.setStock(s);
			p4.setStocks(stocks);
			s.setMappings(mappi);
			mappi.setGeneratedProductId(2547);
			//mappi.setId(122);
			mappi.setLocId(121);
			mappi.setStock(s);
			//mappi.setStockId(123);
			//s.setId(123);
			s.setLiveStockCount(4724);
			s.setProduct(p4);
			//s.setProductId(122);
			epd.setTechDetails("shfgsjhgdjhsdkjgksdjhfgkjhsdfkgjhksdjfgh");
			epd.setDescription("hello world welcome to BLOW!!");
			//epd.setId(124);
			//epd.setProdId(122);
			epd.setProduct(p4);
			det.setBrand("fgsdg");
			det.setColor("sdgsdfg");
			//det.setId(125);
			det.setMaterial("afxbvx");
			det.setPrice(123213);
			//det.setProdId(122);
			det.setProduct(p4);
			det.setProductSize("XXL");
			((Prodcty)p4).getEpd().setBrand("levis");
			((Prodcty)p4).getEpd().setColor("RED");
			((Prodcty)p4).getEpd().setMaterial("COTTON");
			((Prodcty)p4).getEpd().setPrice(1230);
			((Prodcty)p4).getEpd().setProductSize("M");
			list.add(p4);
			//}
			//BlowUtils.getContext().saveOrUpdateEntity(list);
			System.out.println(new Date().getTime()-l);
			//BlowUtils.getContext().saveOrUpdateEntity(p4);
			BlowUtils.getContext().closeSession();
			/*System.out.println(new XStream().toXML( BlowUtils
					.getBasis(Prodcty.class)
					.propEquals("id", p2.getProduct().getId())
					//.propEquals("stock.mappings.id", 124)
					.fetchMode(BlowParam.EAGER)
					.retrieveMany(null)));*///check params==null
			//long temp=new Date().getTime()-l;
			//System.out.println(temp);
			//BlowCore.getInstance().getContext().getSQLResult("getAllProducts", null);
			//BlowCore.getInstance().getContext().getSQLResult("getProducts", null);
			/*System.out.println(new Date().getTime()-l);
			BlowUtils.getContext().openSession();
			for (int i = 0; i < 1000; i++) {

				//System.out.println(new XStream().toXML(BlowCore.getInstance().getContext().getSQLResult("getAllProducts", null)));
				//Map m= new HashMap();
				//m.put("product", p);
				//System.out.println(new XStream().toXML());
				//BlowCore.getInstance().getContext().getSQLResult("getProducts", m);
				Object p=(Object) BlowUtils
						.getBasis(Prodcty.class)
						//.prop(BlowParam.GT, "id", 0).prop(BlowParam.GT, "epd.id", 0)
						//.prop(BlowParam.LIKE_AROUND, "name", "")
						.retrieveMany(null);

				Object p1=(Object) BlowUtils
						.getBasis(Prodcty.class)
						.propEquals("id",121)
						.retrieveOne();

				Object p2=(Object) BlowUtils
						.getBasis(Prodcty.class)
						.propEquals("id",1)
						.retrieveOne();

				Object p3=(Object) BlowUtils
						.getBasis(Prodcty.class)
						.propEquals("id",12)
						.retrieveOne();
			    BlowUtils
						.getBasis(Prodcty.class)
						.propEquals("epd.id",121)
						.propEquals("id", 123)
						.fetchMode(BlowParam.EAGER)
						.retrieveMany(null);
				//System.out.println(new XStream().toXML(p1));
				//System.out.println(new Date().getTime()-l-temp+"         "+i);

			}
			System.out.println(new Date().getTime()-l);
			ConnectionPool.getInstance().printPoolSize();
			BlowUtils.getContext().closeSession();*/
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
