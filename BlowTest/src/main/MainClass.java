package main;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;

import com.sale.util.BlowUtils;
import com.sales.constants.BlowParam;
import com.sales.core.BlowCore;
import com.sales.core.QuerryBuilder;
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






			/*Prodcty p=(Prodcty) BlowUtils
			.getBasis(Prodcty.class)
			.propEquals("id",12)
			.retrieveOne();
			System.out.println(new XStream().toXML(p));


			ProductDetails p1=(ProductDetails) BlowUtils
			.getBasis(ProductDetails.class)
			.propEquals("id",12)
			.retrieveOne();
			System.out.println(new XStream().toXML(p1));


			ElectronicProductDetails p2=(ElectronicProductDetails) BlowUtils
			.getBasis(ElectronicProductDetails.class)
			.propEquals("product.id",13)
			.retrieveOne();
			System.out.println(new XStream().toXML(p2.getProduct()));
			 */
			
			
			
			/*Object p3= BlowUtils
							.getBasis(Prodcty.class)
							//.propEquals("stock.mappings.locId","10")
							.propEquals("id", 121)
							//.propEquals("catId", 2)
							.retrieveOne();
			
			System.out.println(new XStream().toXML(p3));*/
			
			
			
			
			Prodcty p4=new Prodcty();
			p4.setCatId(2000);
			p4.setId(122);
			p4.setName("testy");
			ElectronicProductDetails epd=new ElectronicProductDetails();
			Stock s= new Stock();
			StockMappings mappi=new StockMappings();
			ProductDetails det=new ProductDetails();
			p4.setEpd(epd);
			p4.setDetails(det);
			p4.setStock(s);
			s.setMappings(mappi);
			mappi.setGeneratedProductId(25347);
			mappi.setId(122);
			mappi.setLocId(121);
			mappi.setStock(s);
			mappi.setStockId(123);
			s.setId(123);
			s.setLiveStockCount(3164724);
			s.setProduct(p4);
			s.setProductId(122);
			epd.setTechDetails("shfgsjhgdjhsdkjgksdjhfgkjhsdfkgjhksdjfgh");
			epd.setDescription("hello world welcome to BLOW!!");
			epd.setId(124);
			epd.setProdId(122);
			epd.setProduct(p4);
			det.setBrand("fgsdg");
			det.setColor("sdgsdfg");
			det.setId(125);
			det.setMaterial("afxbvx");
			det.setPrice(123213);
			det.setProdId(122);
			det.setProduct(p4);
			det.setProductSize("XXL");
			((Prodcty)p4).getEpd().setBrand("levis");
			((Prodcty)p4).getEpd().setColor("RED");
			((Prodcty)p4).getEpd().setMaterial("COTTON");
			((Prodcty)p4).getEpd().setPrice(1230);
			((Prodcty)p4).getEpd().setProductSize("M");
			
			
			BlowUtils.getContext().saveOrUpdateEntity(p4);
			
			
			System.out.println(new XStream().toXML( BlowUtils
														.getBasis(Prodcty.class)
														.propEquals("id", 121)
														.fetchMode(BlowParam.EAGER)
														.retrieveOne()));//check params==null
			} catch (NullPointerException e) {
				 e.printStackTrace();
			 } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
