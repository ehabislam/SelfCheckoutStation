package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import unSorted.SelfCheckoutStationInstance;

/**
 * 
 * You can also find a swing tutorial here: https://docs.oracle.com/javase/tutorial/uiswing/TOC.html
 * 
 * @author Chris
 *
 */
public class GUI {
	

	private StationPanel stationPanel;
	
	public GUI( JFrame frame, String name, SelfCheckoutStationInstance station) {
		
		frame.setTitle(name);
		frame.setUndecorated(false);
		frame.setPreferredSize( new Dimension( 640, 480 ) );
		stationPanel= new StationPanel(station.id, station);
		frame.setLayout(new BorderLayout());
		frame.add(stationPanel, BorderLayout.CENTER);
		
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		frame.pack();
		frame.setVisible(true);
	}

	public StationPanel getStationPanel() {
		return stationPanel;
	}
	
}
