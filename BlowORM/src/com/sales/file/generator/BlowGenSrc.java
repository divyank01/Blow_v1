/**
  *  BLOW-ORM is an open source ORM for java and its currently under development.
  *
  *  Copyright (C) 2016  @author Divyank Sharma
  *
  *  This program is free software: you can redistribute it and/or modify
  *  it under the terms of the GNU General Public License as published by
  *  the Free Software Foundation, either version 3 of the License, or
  *  (at your option) any later version.
  *
  *  This program is distributed in the hope that it will be useful,
  *  but WITHOUT ANY WARRANTY; without even the implied warranty of
  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  *  GNU General Public License for more details.
  *
  *  You should have received a copy of the GNU General Public License
  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  *  
  *  
  *  In Addition to it if you find any bugs or encounter any issue you need to notify me.
  *  I appreciate any suggestions to improve it.
  *  @mailto: divyank01@gmail.com
  */
package com.sales.file.generator;

import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;

import com.sales.poolable.parsers.ORM_MAPPINGS_Parser;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS.Maps;
import com.sales.pools.OrmMappingPool;

public class BlowGenSrc {

	public static void main(String[] args) {
		try {
			ORM_MAPPINGS_Parser parser = OrmMappingPool.getInstance().borrowObject();
			ORM_MAPPINGS orm_mapping=parser.getOrm_Mappings();
			ClassFileGenerator c= new ClassFileGenerator();
			Iterator<String> itr=orm_mapping.getMaps().keySet().iterator();
			while(itr.hasNext()){
				String name=itr.next();
				Maps maps=orm_mapping.getMaps().get(name);
				File file=new File(args[0]+maps.getClassName().replaceAll("\\.", Character.toString(File.separatorChar))+".java");
				if(!file.exists()){
					if(file.getParentFile()!=null && !file.getParentFile().exists())
						file.getParentFile().mkdirs();
					file.createNewFile();
					FileWriter fw = new FileWriter(file);
					StringBuilder sb= new StringBuilder();
					c.createTheClass(maps, sb);
					fw.append(sb.toString());
					fw.flush();
					fw.close();
				}
			}
			OrmMappingPool.getInstance().returnObject(parser);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
