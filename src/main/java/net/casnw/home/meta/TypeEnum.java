package net.casnw.home.meta;

public enum TypeEnum {
	
	In {
	        public String getString() {
	            return "In";
	        }
	    },
	Out {
	        public String getString() {
	            return "Out";
	        }
	    },
	InOut {
	        public String getString() {
	            return "InOut";
	        }
	    };
	
	public abstract String getString();//这里是很重要的

}
