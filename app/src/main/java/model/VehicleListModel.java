package model;

import java.util.ArrayList;
import java.util.Map;

public class VehicleListModel  {


    private ArrayList<Position> positions;
    private Map<Long, Device> devicesMap;

    public VehicleListModel(ArrayList<Position> positions, Map<Long, Device> devicesMap) {
        this.positions = positions;
        this.devicesMap = devicesMap;
    }


    public ArrayList<Position> getPositions() {
        return positions;
    }

    public void setPositions(ArrayList<Position> positions) {
        this.positions = positions;
    }

    public Map<Long, Device> getDevicesMap() {
        return devicesMap;
    }

    public void setDevicesMap(Map<Long, Device> devicesMap) {
        this.devicesMap = devicesMap;
    }
}
