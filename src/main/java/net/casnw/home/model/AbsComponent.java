//@DECLARE@
package net.casnw.home.model;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;

import org.apache.commons.collections.map.ListOrderedMap;

import net.casnw.home.meta.ModuleMetaObj;
import net.casnw.home.meta.SpacescaleEnum;
import net.casnw.home.meta.SparefsysEnum;
import net.casnw.home.meta.TimescaleEnum;
import net.casnw.home.meta.VariableMeta;
import net.casnw.home.meta.VariableMetaObj;
import net.casnw.home.meta.metaParse;
import net.casnw.home.poolData.Datable;
import prms.api.RedisUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.casnw.home.util.OftenTools;
import java.lang.reflect.Field;

/**
 * 抽象模块
 *
 * @author myf@lzb.ac.cn
 * @since 2013-04-10
 * @version 1.0
 *
 */
public abstract class AbsComponent implements Componentable {

	private Contextable _context = null;
	private Modelable _model = null;
	private String _name = getClass().getName();

	public String state = "create";

	ModuleMetaObj mmo;
	JSONObject mapTable = new JSONObject();

	public ModuleMetaObj getModuleMetaObject(String moduleclass) {
		try {

			mmo = metaParse.parseModuleMeta(moduleclass);
			System.out.println("initCollection method " + moduleclass);
			// create Element object
			// }

		} catch (ClassNotFoundException ex) {
			System.out.println(moduleclass + "class not found");
			// Exceptions.printStackTrace(ex);
		}

		return mmo;

	}

	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		state = "init";

	}

	@Override
	public void run() throws Exception {
		// TODO Auto-generated method stub
		state = "run";
	}

	@Override
	public void clear() throws Exception {
		// TODO Auto-generated method stub
		state = "clear";
	}

	@Override
	public Modelable getModel() {
		// TODO Auto-generated method stub

		return this._model;

	}

	public JSONObject getMapTable() {
		// TODO Auto-generated method stub
		return this.mapTable;
	}

	public void setMapTable(JSONObject mt) {

		this.mapTable = mt;

	}

	@Override
	public Contextable getContext() {
		// TODO Auto-generated method stub
		return this._context;
	}

	@Override
	public void setModel(Modelable model) {
		if (model != null) {
			_model = model;
		}
	}

	@Override
	public void setContext(Contextable context) {
		if (context != null) {
			_context = context;
		}

	}

	@Override
	public void setInstanceName(String name) {
		_name = name;
	}

	@Override
	public String getInstanceName() {
		return _name;
	}

	@Override
	public String getMetadata() {
		mmo = getModuleMetaObject(this.getClass().toString().split(" ")[1]);

		JSONObject json = new JSONObject();
		json.put("moduleClass", mmo.getModuleClass());
		json.put("name", mmo.getName());
		json.put("author", mmo.getAuthor());
		json.put("keyword", mmo.getKeyword());
		json.put("description", mmo.getDescription());
		json.put("timeScale", mmo.getTimeScale());
		json.put("spaceScale", mmo.getSpaceScale());
		json.put("version", mmo.getVersion());
		json.put("category", mmo.getCategory());
		json.put("applicationField", mmo.getApplicationField());
		json.put("theory", mmo.getTheory());
		json.put("spaRefSys", mmo.getSpaRefSys());

		List<VariableMetaObj> vmlist = mmo.getVarMetaObjList();

		List<Map> ttmaps = new ArrayList<>();
		for (VariableMetaObj vmo : vmlist) {
			Map<String, Object> tts = new HashMap<String, Object>();
			tts.put("dataType", vmo.getDataType());
			tts.put("description", vmo.getDescription());
			tts.put("unit", vmo.getUnit());
			tts.put("range", vmo.getRange());
			tts.put("value", vmo.getValue());
			tts.put("size", vmo.getSize());
			tts.put("context", vmo.getContext());
			tts.put("date", vmo.getDate());
			tts.put("name", vmo.getName());
			ttmaps.add(tts);

		}
		json.put("variables", ttmaps);

		return json.toString();
	}

	@Override
	public String getInputAttributes() {
		mmo = getModuleMetaObject(this.getClass().toString().split(" ")[1]);

		JSONObject json = new JSONObject();
		List<VariableMetaObj> vmlist = mmo.getVarMetaObjList();
		List<Map> ttmaps = new ArrayList<>();
		for (VariableMetaObj vmo : vmlist) {
			if (vmo.getType().startsWith("In")) {
				Map<String, Object> tts = new HashMap<String, Object>();
				tts.put("name", vmo.getName());
				tts.put("dataType", vmo.getDataType());
				tts.put("description", vmo.getDescription());
				tts.put("unit", vmo.getUnit());
				tts.put("range", vmo.getRange());
				tts.put("value", vmo.getValue());
				tts.put("size", vmo.getSize());
				tts.put("context", vmo.getContext());
				tts.put("date", vmo.getDate());
				ttmaps.add(tts);
			}
		}
		json.put("variables", ttmaps);

		return json.toString();
	}

	@Override
	public String getOutputAttributes() {
		mmo = getModuleMetaObject(this.getClass().toString().split(" ")[1]);

		JSONObject json = new JSONObject();
		List<VariableMetaObj> vmlist = mmo.getVarMetaObjList();
		List<Map> ttmaps = new ArrayList<>();
		for (VariableMetaObj vmo : vmlist) {
			if (vmo.getType().endsWith("Out")) {
				Map<String, Object> tts = new HashMap<String, Object>();
				tts.put("name", vmo.getName());
				tts.put("dataType", vmo.getDataType());
				tts.put("description", vmo.getDescription());
				tts.put("unit", vmo.getUnit());
				tts.put("range", vmo.getRange());
				tts.put("value", vmo.getValue());
				tts.put("size", vmo.getSize());
				tts.put("context", vmo.getContext());
				tts.put("date", vmo.getDate());
				ttmaps.add(tts);
			}
		}
		json.put("variables", ttmaps);

		return json.toString();
	}

	@Override
	public String getAttributeValue(String myinput) {

		Field comField;
		String value = "";

		try {
			comField = this.getClass().getField(myinput);
			Datable obj = (Datable)comField.get(this);
			if (obj != null) {
				value = obj.toString();
			}

			return value;
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return value;
	}

	@Override
	public void setAttributeValue(String name, String value) {
		Field comField;
		String size;
		Datable dataObject = null;
		VariableMetaObj vmo;
		// mmo = getModuleMetaObject(this.getClass().toString().split(" ")[1]);

		try {
		//	System.out.println("222compname=" + this.getClass());
			comField = this.getClass().getField(name);
			vmo = metaParse.paseVarmeta(comField);
			String dataType = vmo.getDataType();
			size = Integer.toString(vmo.getSize());
			dataObject = OftenTools.getDataObject(dataType, size);
			dataObject.setValue(value);

			comField.set(this, dataObject);

		//	System.out.println("222set successful");
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public String getState() {
		return _name;
	}

	@Override
	public void setState(String state, int value) {

	}

	public void initValue(RedisUtil redisUtil) {


		Iterator<String> itr = mapTable.keys();
		while (itr.hasNext()) {
			String attriComName = itr.next();
			String attriContextName = mapTable.getString(attriComName);
			//System.out.println("222 attriContextName=" + attriContextName);

			// 判断是否为空
			if (redisUtil.get(attriContextName) != null) {
				String value= redisUtil.get(attriContextName).toString();
				if(!"".equalsIgnoreCase(value)&&!"null".equalsIgnoreCase(value)) {
					this.setAttributeValue(attriComName, value);
				//	System.out.println("2211 attriName=" + attriComName + " attriContextName=" + attriContextName + " value="
				//			+ value);
				}
			}
		}
	}

	public void setValue2Redis(RedisUtil redisUtil) {
		Iterator<String> itr = mapTable.keys();
		while (itr.hasNext()) {
			String attriComName = itr.next();
			String attriContextName = mapTable.getString(attriComName);
		//	System.out.println("333 attricontextname=" + attriComName+" value =" + getAttributeValue(attriComName));
			if (getAttributeValue(attriComName) != null && !"".equalsIgnoreCase(getAttributeValue(attriComName))) {
				
				redisUtil.set(attriContextName, getAttributeValue(attriComName));
			}
		}

	}

}
