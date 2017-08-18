package main;

import java.util.List;

import com.sale.util.BlowUtils;
import com.sales.constants.BlowParam;
import com.sales.core.BlowContext;
import com.sales.core.BlowCoreMapper;
import com.sales.pojo.Prodcty;
import com.sales.pojo.ProductDetails;
import com.thoughtworks.xstream.XStream;

public class NewMain {

	public static void main(String[] args) {
		try {
		BlowUtils.getContext().openSession();
		
		BlowContext ctx=BlowUtils.getContext();
		List p=ctx.getBasis(ProductDetails.class)
						.prop(BlowParam.EQ, "id", "10")
						.prop(BlowParam.EQ, "id", "11")
						.retrieveMany();
		System.out.println(new XStream().toXML(p));
		
		Prodcty prod= new Prodcty();
		prod.setId(11);
		prod.setName("lol temp prod");
		prod.setCatId(2344);
		ctx.saveOrUpdateEntity(prod);
		
		BlowUtils.getContext().closeSession();
		}catch(Exception e) {
			e.printStackTrace();
			try {
				BlowUtils.getContext().rollback();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

}
