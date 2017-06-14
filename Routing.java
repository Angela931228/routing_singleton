package rcs.routing;

import java.util.*;

public class Routing {
	private Layout layout = Layout.getInstance();
	private PriorityQueue<Route> priorityQueue = new PriorityQueue<>(layout.size(), (route1, route2) -> (int)(route1.getCost() - route2.getCost()));
	private Set<Integer> visitedGridIdSet = new HashSet<>(layout.size());
	
	public Routing() {
	}
	
	// Uniform-Cost Search (UCS) Algorithm
	public Optional<Route> getShortestPath(int sourceGridId, int destinationGridId) {
		priorityQueue.add(new Route(sourceGridId));
		
		while(!priorityQueue.isEmpty()) {
			Route route = priorityQueue.remove();
			Integer gridId = route.getLastGridId();
			
			if(gridId == destinationGridId) {
				return Optional.of(route);
			}
			
			visitedGridIdSet.add(gridId);
			List<Integer> neighborGridIdList = layout.getNeighborGridIdList(gridId);
			
			for(Integer neighborGridId : neighborGridIdList) {
				if(!visitedGridIdSet.contains(neighborGridId)) {
					route.add(neighborGridId, layout.getCost(neighborGridId));
					priorityQueue.add(route);
				}
			}
		}
		
		return Optional.empty();
	}
	
//	private void evaluateneighbors(Route evaluationPath) {
//		gridIdCostMap.forEach((destinationGridId, cost) -> {
//			if(!visitedGridIdSet.contains(destinationGridId)) {
//				GridType gridType = layout.getGridType(destinationGridId);
//				
//				if(gridType != GridType.BLOCK) {
//					double d1 = gridIdCostMap.get(evaluationPath.getGridId());
//					double d2 = layout.getCost(destinationGridId);
//					double newCost = d1 + d2;
//					
//					if(newCost < cost) {
//						gridIdCostMap.put(destinationGridId, newCost);
//					}
//					
//					priorityQueue.add(new Route(destinationGridId, newCost));
//				}
//			}
//		});
//	}
	
	public static void main(String[] args) throws Exception {
		Routing routing = new Routing();
		Optional<Route> optional = routing.getShortestPath(23, 361);
		System.out.println(optional);
	}
}
