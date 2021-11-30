package gui;

import javax.swing.JFrame;

import unSorted.SelfCheckoutStationInstance;

/*
 * Instantiates the self checkout stations along with
 * the GUIs, essentially starting the whole process.
 */
public class GUISetUp {
	
	public static void main(String[] args) {
		SelfCheckoutStationInstance station1 = new SelfCheckoutStationInstance(1);
		SelfCheckoutStationInstance station2 = new SelfCheckoutStationInstance(2);
		GUIHelper.getStationList().add(station1);
		GUIHelper.getStationList().add(station2);
		
		JFrame station1GUI = station1.scs.screen.getFrame();
		new GUI(station1GUI, "Station1", station1);
		JFrame station2GUI = station2.scs.screen.getFrame();
		new GUI(station2GUI, "Station2", station2);
		
		ControlPanel.getInstance();
		HardwareSimulator.getInstance();
	}

}
