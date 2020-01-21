package prms.api;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.casnw.home.model.AbsComponent;
import net.sf.json.JSONObject;


@RestController
@EnableAutoConfiguration


@RequestMapping("/prms/SrunoffSmidx")
public class PRMSSrunoffSmidxController
{ 
	protected final Log _logger = LogFactory.getLog(getClass());
	
	String moduleClass = "prms.process.SrunoffSmidx";
	
	AbsComponent com = (AbsComponent) loadComponent(moduleClass);	
	@Resource
	RedisUtil redisUtil;
	
	public AbsComponent loadComponent(String className)  {

        AbsComponent component = null;
        ClassLoader hclassloader;
        Class<?> clazz;

        _logger.info("loadComponent begin :" + className);

        if (!"".equalsIgnoreCase(className) && className != null) {
            hclassloader = ClassLoader.getSystemClassLoader();
            try {
                clazz = Class.forName(className);
                System.out.println("path=" + clazz.getResource("").getPath());

                this.getClass().getResource("").getPath();
                clazz = hclassloader.loadClass(className);
            } catch (Exception e) {
                _logger.info(className + " is not exist");
                return null;
            }
            // generate an instance of that class
            if (clazz != null) {
                try {
					component = (AbsComponent) clazz.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

            }
        }
        _logger.info("loadComponent end :" + className);
        return component;
    }	
	
	@RequestMapping(value="/",method = RequestMethod.GET)
	public String moduleMetadata(){
		_logger.info("get module metadata of " + moduleClass);
		return com.getMetadata();		
	}
	
	@RequestMapping(value="/inputs/",method = RequestMethod.GET)
	public String getInputAttributes(){
		
		return com.getInputAttributes();
	}
	
	@RequestMapping(value="/outputs/",method = RequestMethod.GET)
	public String getOutputAttributes(){
		return com.getOutputAttributes();
	}	

	@RequestMapping(value="/inputs/get")
	public String getInputAttribute(HttpServletRequest request ){
		String field = request.getParameter("t");
		return com.getAttributeValue(field);
	}

	@RequestMapping(value="/inputs/set")
	public void setInputAttribute(HttpServletRequest request ){
		String field = request.getParameter("t");
		String value = request.getParameter("v");
		com.setAttributeValue(field,value);
	}

	@RequestMapping(value="/outputs/{output}",method = RequestMethod.GET)
	public String getOutputAttribute(@PathVariable String output){
		return com.getAttributeValue(output);
	}

	@RequestMapping(value="/outputs/{output}",method = RequestMethod.POST)
	public void setOutputAttribute(@PathVariable String output, @RequestParam("value") String value){
		com.setAttributeValue(output,value);
	}
	
	@RequestMapping(value="/state/",method = RequestMethod.GET)
	public String getState(){
		
		 return com.state;
	}
	
	@RequestMapping(value="/state/init")
	public void setInitState(HttpServletRequest request, @RequestParam("mapTable") String mapTable) throws Exception{
			
		_logger.info(moduleClass+" aaa   init start");
		System.out.println("mapTable="+mapTable);
		com.setMapTable(JSONObject.fromObject(mapTable));
		com.initValue(redisUtil);
		com.init();
		com.setValue2Redis(redisUtil);
		_logger.info(moduleClass+" init end");
		
	}
	
	@RequestMapping(value="/state/run")
	public void setRunState() throws Exception{
		//	_logger.info(moduleClass+" run start");
		com.initValue(redisUtil);
		com.run();
		com.setValue2Redis(redisUtil);
	//	_logger.info(moduleClass+" rnn end");
		
	}

	
	@RequestMapping(value="/state/re-running",method = RequestMethod.PUT)
	public void setRerunState(@RequestParam("value") String value) throws Exception{
		
		_logger.info(moduleClass+" re-running start");
		com = (AbsComponent) loadComponent(moduleClass);
	//	com.initValue(value);
		com.run();
		_logger.info(moduleClass+" re-running end");
		
		
	}
	
}
