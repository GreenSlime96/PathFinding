import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import ui.Window;

public class Main {
	public static void main(String[] args) {		
		// use system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("unable to use system look and feel");
		}

		// run in event thread
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final Window window = new Window();
				window.pack();
				window.setLocationRelativeTo(null);
				window.setVisible(true);
			}
		});
	}
}