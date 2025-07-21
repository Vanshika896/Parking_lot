import java.util.*;

class Solution {

    private Helper helper;
    private int[][][] parking;
    private int floors, rows, cols;
    private Map<String, String> vehicleToSpot; // vehicleNumber/ticketId -> spotId
    private Map<String, Integer> spotToVehicleType; // spotId -> vehicleType (2 or 4)
    private Map<Integer, Map<Integer, Set<String>>> freeSpots; // floor -> vehicleType -> set of spotIds

    public void init(Helper helper, int[][][] parking) {
        this.helper = helper;
        this.parking = parking;
        this.floors = parking.length;
        this.rows = parking[0].length;
        this.cols = parking[0][0].length;

        this.vehicleToSpot = new HashMap<>();
        this.spotToVehicleType = new HashMap<>();
        this.freeSpots = new HashMap<>();

        for (int f = 0; f < floors; f++) {
            Map<Integer, Set<String>> typeMap = new HashMap<>();
            typeMap.put(2, new HashSet<>());
            typeMap.put(4, new HashSet<>());
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    int type = parking[f][r][c];
                    if (type == 2 || type == 4) {
                        String spotId = f + "-" + r + "-" + c;
                        typeMap.get(type).add(spotId);
                    }
                }
            }
            freeSpots.put(f, typeMap);
        }
    }

    public String park(int vehicleType, String vehicleNumber, String ticketId, int parkingStrategy) {
        String spotId = null;

        if (parkingStrategy == 0) {
            outer:
            for (int f = 0; f < floors; f++) {
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        if (parking[f][r][c] == vehicleType) {
                            String id = f + "-" + r + "-" + c;
                            if (freeSpots.get(f).get(vehicleType).contains(id)) {
                                spotId = id;
                                break outer;
                            }
                        }
                    }
                }
            }
        } else {
            int maxCount = -1;
            int selectedFloor = -1;
            for (int f = 0; f < floors; f++) {
                int count = freeSpots.get(f).get(vehicleType).size();
                if (count > maxCount) {
                    maxCount = count;
                    selectedFloor = f;
                }
            }
            if (selectedFloor != -1 && maxCount > 0) {
                for (int r = 0; r < rows && spotId == null; r++) {
                    for (int c = 0; c < cols && spotId == null; c++) {
                        if (parking[selectedFloor][r][c] == vehicleType) {
                            String id = selectedFloor + "-" + r + "-" + c;
                            if (freeSpots.get(selectedFloor).get(vehicleType).contains(id)) {
                                spotId = id;
                            }
                        }
                    }
                }
            }
        }

        if (spotId != null) {
            vehicleToSpot.put(vehicleNumber, spotId);
            vehicleToSpot.put(ticketId, spotId);
            spotToVehicleType.put(spotId, vehicleType);
            String[] parts = spotId.split("-");
            int f = Integer.parseInt(parts[0]);
            freeSpots.get(f).get(vehicleType).remove(spotId);
        }

        return spotId;
    }

    public boolean removeVehicle(String spotId) {
        if (!spotToVehicleType.containsKey(spotId)) return false;

        int vehicleType = spotToVehicleType.get(spotId);
        String[] parts = spotId.split("-");
        int f = Integer.parseInt(parts[0]);
        int r = Integer.parseInt(parts[1]);
        int c = Integer.parseInt(parts[2]);

        freeSpots.get(f).get(vehicleType).add(spotId);

        vehicleToSpot.values().removeIf(spot -> spot.equals(spotId));
        spotToVehicleType.remove(spotId);
        return true;
    }

    public String searchVehicle(String query) {
        return vehicleToSpot.getOrDefault(query, "");
    }

    public int getFreeSpotsCount(int floor, int vehicleType) {
        if (!freeSpots.containsKey(floor) || !freeSpots.get(floor).containsKey(vehicleType)) return 0;
        return freeSpots.get(floor).get(vehicleType).size();
    }
}

interface Helper {
    void print(String s);
    void println(String s);
}
