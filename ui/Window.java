package ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import core.Model;

public class Window extends JFrame {

	private static final long serialVersionUID = 1699819756325525388L;
	
	public Window() {
		super();
		
		setTitle("PathFinding Demonstration");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final Model model = new Model();
		
		final Controls controls = new Controls(model);
		final View view = new View(model);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(controls, BorderLayout.EAST);
		panel.add(view, BorderLayout.CENTER);
		
		setContentPane(panel);
		
		pack();
		
		view.setVisible(true);
	}
}