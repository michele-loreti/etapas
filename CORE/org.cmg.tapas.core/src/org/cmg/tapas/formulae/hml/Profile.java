package org.cmg.tapas.formulae.hml;

import java.util.HashMap;
import java.util.Map.Entry;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.pa.Process;

public class Profile<X extends Process<X, Y>,Y extends ActionInterface> {
	
	public HashMap<X, HashMap<String, Integer>> mem;
	private static Profile instance;
	
	private Profile(){
		mem = new HashMap<X, HashMap<String, Integer>>();
	}
	
	public static Profile getInstace(){
		if(instance == null)
			instance = new Profile();
			
		return instance;
	}
	
	public boolean containsState(X s){
		return mem.containsKey(s);
	}
	
	public void add(X s, String f){
		if(!mem.containsKey(s))
			mem.put(s, new HashMap<String, Integer>());

		if(!mem.get(s).containsKey(f))
			mem.get(s).put(f, 0);
		
		Integer i = mem.get(s).get(f);
		mem.get(s).put(f, i+1);
	}
	
	public String toString(){
		String res = "";
		for (Entry<X, HashMap<String, Integer>> e1 : mem.entrySet()) {
			res += e1.getKey().hashCode()+":[\n";
			for (Entry<String, Integer> e2 : e1.getValue().entrySet()) {
				res += "\t <"+e2.getKey().hashCode()+", "+e2.getValue()+">\n";
			} 
			res += "]\n";
		}	
		return res;
	}
	
	public int getInt(X s, String f){
		return mem.get(s).get(f);
	}
}



