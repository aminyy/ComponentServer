package prms.api;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.casnw.home.runtime.Runtime;


@RestController
@EnableAutoConfiguration

@RequestMapping("/redis")
public class RedisRest {
	@Resource
	RedisUtil redisUtil;

	@RequestMapping(value = "/set", method = { RequestMethod.GET })
	public String set(@RequestParam String value) {
		
	//	args[0] = "D:\\workspace\\run\\prms4.hom";
     //  // if (args != null && !"".equalsIgnoreCase(args[0])) {
         //   propertiesFile = args[0];
      //  }
		
		
		redisUtil.set("testredis", value);

		return "0000";
	}

	@RequestMapping(value = "/get", method = { RequestMethod.GET })
	public String get() {
		String value = (String) redisUtil.get("testredis");

		return value;
	}
}
