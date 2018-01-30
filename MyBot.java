import hlt.*;

import java.util.*;

public class MyBot {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("Tamagocchi");
        final ArrayList<Move> moveList = new ArrayList<>();
              
        	
        for (;;) {
            moveList.clear();
            gameMap.updateMap(Networking.readLineIntoMetadata());
            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
            	TreeMap<Double, Planet> distancetoPlanets = new TreeMap<Double, Planet>();
                TreeMap<Double, Ship> distancetoShips = new TreeMap<Double, Ship>();
                int myID = gameMap.getMyPlayerId();
                if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
                    continue;
                }
                for(final Ship enemyship : gameMap.getAllShips()){
                	if(enemyship.getOwner() != myID){
                		distancetoShips.put(ship.getDistanceTo(enemyship), enemyship);
                	}
                }
            	for (final Planet planets : gameMap.getAllPlanets().values()) {
            		distancetoPlanets.put(ship.getDistanceTo(planets), planets);
            	}
            	Ship closestShip = distancetoShips.firstEntry().getValue();
            	Double dclosestShip = distancetoShips.firstEntry().getKey();
            	for (Map.Entry<Double, Planet> planetEntry : distancetoPlanets.entrySet()) {
            		Planet planet = planetEntry.getValue();
            		Double dplanet = planetEntry.getKey();
            		if(dplanet <= (dclosestShip - 12)){
                		if (planet.isOwned() && planet.isFull()) {
                			continue;
                		}
                		if (planet.isOwned() && (planet.getOwner() != myID) ) {
                			continue;
                		}
                		if (ship.canDock(planet)) {
                			moveList.add(new DockMove(ship, planet));
                			break;
                		}
	                    final ThrustMove newThrustMove = Navigation.navigateShipToDock(gameMap, ship, planet, Constants.MAX_SPEED);
	                    if (newThrustMove != null) {
	                        moveList.add(newThrustMove);
	                    }
	                    break;
            		}
            		else{
            			final ThrustMove newThrustMove = Navigation.navigateShipTowardsTarget(gameMap, ship, new Position(closestShip.getXPos(), closestShip.getYPos()), 7, true, 180, Math.PI/180.0, 1);
            			if(newThrustMove != null){
            				moveList.add(newThrustMove);
            				break;
            			}
            		}
            	}
            }
            Networking.sendMoves(moveList);
        }
    }
}
