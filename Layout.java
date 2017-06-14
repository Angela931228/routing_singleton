package rcs.routing;

import java.util.*;
import java.util.stream.*;
import com.google.common.collect.*;
import rcs.robot.*;
import rcs.utils.*;

public class Layout {
	private static final Layout LAYOUT = new Layout();
	
	private int row = LayoutConfig.getRow();
	private int column = LayoutConfig.getColumn();
	private List<Integer> gridIdList = new ArrayList<>(row * column);
	private ArrayTable<Integer, Integer, GridType> gridTypeTable = ArrayTable.create(
		Streams.mapWithIndex(IntStream.of(new int[row]), (i, index) -> (int)index).collect(Collectors.toList()),
		Streams.mapWithIndex(IntStream.of(new int[LayoutConfig.getColumn()]), (i, index) -> (int)index).collect(Collectors.toList())
	);
	private ArrayTable<Integer, Integer, Double> costTable = ArrayTable.create(gridTypeTable.rowKeyList(), gridTypeTable.columnKeyList());
	
	private Layout() {
		for(int i = 1, size = row * column; i <= size; i++) {
			gridIdList.add(i);
		}
		
		for(int rowIndex = 0; rowIndex < row; rowIndex++) {
			for(int columnIndex = 0; columnIndex < column; columnIndex++) {
				gridTypeTable.set(rowIndex, columnIndex, GridType.BLOCK);
			}
		}
		
		LayoutConfig.getPathList().forEach(gridId -> gridTypeTable.set(toRowIndex(gridId), toColumnIndex(gridId), GridType.PATH));
		LayoutConfig.getRackList().forEach(gridId -> gridTypeTable.set(toRowIndex(gridId), toColumnIndex(gridId), GridType.RACK));
		LayoutConfig.getWorkstationList().forEach(gridId -> gridTypeTable.set(toRowIndex(gridId), toColumnIndex(gridId), GridType.WORKSTATION));
		LayoutConfig.getChargingStationList().forEach(gridId -> gridTypeTable.set(toRowIndex(gridId), toColumnIndex(gridId), GridType.CHARGING_STATION));
		
		for(int rowIndex = 0; rowIndex < row; rowIndex++) {
			for(int columnIndex = 0; columnIndex < column; columnIndex++) {
				costTable.set(rowIndex, columnIndex, getCost(gridTypeTable.at(rowIndex, columnIndex)));
			}
		}
	}
	
	public static Layout getInstance() {
		return LAYOUT;
	}
	
	public int size() {
		return gridIdList.size();
	}
	
	public int distance(int gridId1, int gridId2) {
		int rowDistance = Math.abs(toRowIndex(gridId1) - toRowIndex(gridId2));
		int columnDistance = Math.abs(toColumnIndex(gridId1) - toColumnIndex(gridId2));
		return rowDistance + columnDistance;
	}
	
	public List<Integer> getGridList() {
		return gridIdList;
	}
	
	public GridType getGridType(int gridId) {
		GridType gridType = gridTypeTable.at(toRowIndex(gridId), toColumnIndex(gridId));
		return gridType != null ? gridType : GridType.BLOCK;
	}
	
	public double getCost(int gridId) {
		return costTable.at(toRowIndex(gridId), toColumnIndex(gridId));
	}
	
	public List<Integer> getNeighborGridIdList(int gridId) {
		List<Integer> neighborList = new ArrayList<>();
		int rowIndex = toRowIndex(gridId);
		int columnIndex = toColumnIndex(gridId);
		nextGridId(rowIndex, columnIndex, LayoutConfig.getRowDirection(rowIndex)).filter(nextGridId -> getGridType(nextGridId) != GridType.BLOCK).ifPresent(neighborList::add);
		nextGridId(rowIndex, columnIndex, LayoutConfig.getColumnDirection(columnIndex)).filter(nextGridId -> getGridType(nextGridId) != GridType.BLOCK).ifPresent(neighborList::add);
		return neighborList;
	}
	
	private int toRowIndex(int gridId) {
		return (gridId - 1) / column;
	}
	
	private int toColumnIndex(int gridId) {
		return (gridId - 1) % column;
	}
	
	private Optional<Integer> toGridId(int rowIndex, int columnIndex) {
		if(rowIndex >= 0 && columnIndex >= 0 && rowIndex < row && columnIndex < column) {
			return Optional.of(rowIndex * column + columnIndex + 1);
		} else {
			return Optional.empty();
		}
	}
	
	private Optional<Integer> nextGridId(int rowIndex, int columnIndex, Direction direction) {
		switch(direction) {
			case UP:
				return toGridId(rowIndex + 1, columnIndex);
			case DOWN:
				return toGridId(rowIndex - 1, columnIndex);
			case LEFT:
				return toGridId(rowIndex, columnIndex - 1);
			case RIGHT:
				return toGridId(rowIndex, columnIndex + 1);
			default:
				return Optional.empty();
		}
	}
	
	private Double getCost(GridType gridType) {
		switch(gridType) {
			case BLOCK:
				return Utils.BLOCK_COST;
			case PATH:
				return Utils.PATH_COST;
			case RACK:
				return Utils.RACK_COST;
			case WORKSTATION:
				return Utils.WORKSTATION_COST;
			case CHARGING_STATION:
				return Utils.CHARGING_STATION_COST;
			default:
				throw new RuntimeException(String.format("Cost not found [Grid Type=%s]", gridType));
		}
	}
}
