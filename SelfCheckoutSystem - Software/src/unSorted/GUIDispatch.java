package unSorted;

import gui.ControlPanel;
import gui.StationPanel;

/*
 * Responsible for dispatching updates to the GUI of the corresponding 
 * self checkout station in response to state changes such as 
 * balance, items added etc. 
 * Also dispatches UI updates to the control panel to inform
 * of required maintenance and servicing.
 */
public class GUIDispatch {
	
	private StationPanel stationPanel;

	public GUIDispatch(StationPanel stationPanel) {
		this.stationPanel = stationPanel;
	}

	public void updateMaintenanceList(int stationId) {
		ControlPanel.getInstance().rerenderMaintenanceList(stationId);
	}
	
	public void updateItemListPanel() {
		stationPanel.updateItemListPanel();
	}

}
