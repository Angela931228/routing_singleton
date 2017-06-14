package rcs.routing;

import java.util.*;
import org.apache.commons.lang3.builder.*;

public class Route {
	private List<Integer> gridIdList = new ArrayList<>();
	private double cost = Double.MAX_VALUE;
	
	public Route(Integer gridId) {
		gridIdList.add(gridId);
	}
	
	public void add(Integer gridId, double cost) {
		gridIdList.add(gridId);
		
		if(this.cost == Double.MAX_VALUE) {
			this.cost = cost;
		} else {
			this.cost += cost;
		}
	}
	
	public Integer getLastGridId() {
		return gridIdList.get(gridIdList.size() - 1);
	}
	
	public List<Integer> getGridIdList() {
		return gridIdList;
	}
	
	public double getCost() {
		return cost;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}