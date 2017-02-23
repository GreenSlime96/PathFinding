package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import algorithms.Search;
import core.Model;

public class Controls extends Box implements Observer, ActionListener, ChangeListener {
	
	private static final long serialVersionUID = 4393439353746474082L;
	
	// ==== Properties ====
	
	private final Model model;
	
	private final JComboBox<String> algorithmComboBox = new JComboBox<String>();
	private final JComboBox<String> diagonalComboBox = new JComboBox<String>();
	private final JComboBox<String> heuristicComboBox = new JComboBox<String>();
	private final JButton startButton = new JButton("Start Search");
	private final JButton pauseButton = new JButton("Pause Search");
	private final JButton clearButton = new JButton("Clear Walls");
	private final JButton fitButton = new JButton("Reset Grid");
	private final JButton mazeButton = new JButton("Create Maze");
	private final JSlider delaySlider = new JSlider(0, 50);
	private final JCheckBox biCheckBox = new JCheckBox("Use Bi-Directional Search");
	
	// ==== Constructor ====
	
	public Controls(Model model) {
		super(BoxLayout.Y_AXIS);
		
        setPreferredSize(new Dimension(280, 600));
        setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 1, 0, 0, new Color(150, 150, 150)),
            new EmptyBorder(20, 20, 20, 20)));
        
		this.model = model;
		model.addObserver(this);
		
		delaySlider.setMinorTickSpacing(1);
		delaySlider.setMajorTickSpacing(5);
		delaySlider.setPaintTicks(true);
		delaySlider.setPaintLabels(true);
		delaySlider.setSnapToTicks(true);
		
		algorithmComboBox.addItem("A-Star");
		algorithmComboBox.addItem("Breadth First");
		algorithmComboBox.addItem("Depth First");
		algorithmComboBox.addItem("Best First");
		algorithmComboBox.addItem("Dijkstra");

		diagonalComboBox.addItem("Always");
		diagonalComboBox.addItem("Never");
		diagonalComboBox.addItem("If At Most One Obstacle");
		diagonalComboBox.addItem("Only When No Obstacles");
		
		heuristicComboBox.addItem("Manhattan Distance");
		heuristicComboBox.addItem("Euclidean Distance");
		heuristicComboBox.addItem("Octile Distance");
		heuristicComboBox.addItem("Chebyshev Distance");
		
		addSetting(algorithmComboBox, "Algorithm", "The type of search algorithm to use");
		addSetting(heuristicComboBox, "Heuristic", "The type of heuristic to use");
		addSetting(diagonalComboBox, "Diagonals", "Whether or not diagonal searching is allowed");
		
		JPanel buttons = new JPanel(new GridLayout(2, 2, 2, 2));
		buttons.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		buttons.add(startButton);
		buttons.add(clearButton);
		buttons.add(fitButton);
		buttons.add(mazeButton);
		addSetting(buttons, "Search Settings", "Starts/stops the search and handles on-screen events");
		
		addSetting(delaySlider, "Search Delay", "The delay between processing nodes in milliseconds");
		addSetting(biCheckBox, "Bidirectional Search", "Starts the search from both the start and goal states");
		
		// vertical spacing
		add(new JPanel(new GridBagLayout()));
		
		algorithmComboBox.addActionListener(this);
		heuristicComboBox.addActionListener(this);
		diagonalComboBox.addActionListener(this);
		startButton.addActionListener(this);
		clearButton.addActionListener(this);
		pauseButton.addActionListener(this);
		fitButton.addActionListener(this);
		mazeButton.addActionListener(this);
		
		delaySlider.addChangeListener(this);
		
		biCheckBox.addActionListener(this);
	}
	
	// ==== Private Helper Methods ====
	
	protected void addSetting(JComponent control, String title, String text) {
		JLabel label = new JLabel(title);
		label.setFont(label.getFont().deriveFont(Font.BOLD));

		control.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		
		JTextArea help = createHelpLabel(text);
		help.setFont(label.getFont().deriveFont(Font.ITALIC, 10));
	
		add(label);
		add(Box.createRigidArea(new Dimension(0, 3)));
		add(control);
		add(Box.createRigidArea(new Dimension(0, 2)));
		add(help);
		add(Box.createRigidArea(new Dimension(0, 15)));
	}
	
    private static JTextArea createHelpLabel(String text) {
        JTextArea textArea = new JTextArea();
        textArea.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        textArea.setEditable(false);
        textArea.setCursor(null);
        textArea.setOpaque(false);
        textArea.setFocusable(false);
        textArea.setText(text);
        textArea.setMaximumSize(new Dimension(300, 400));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }

	// ==== Observer Implementaiton ====
	
	@Override
	public void update(Observable o, Object arg) {
		if (o == model) {
			algorithmComboBox.setSelectedIndex(model.getSearchAlgorithm());
			diagonalComboBox.setSelectedIndex(model.getDiagonalMovement());
			heuristicComboBox.setSelectedIndex(model.getSearchHeuristic());
			biCheckBox.setSelected(model.getBiSearch());
		}		
	}
	
	
	// ==== ActionListener Implementation ====
	
	@Override
	public void actionPerformed(ActionEvent e) {
		final Object source = e.getSource();
		
		if (source == algorithmComboBox) {
			model.setSearchAlgorithm(algorithmComboBox.getSelectedIndex());
		} else if (source == diagonalComboBox) {
			model.setDiagonalMovement(diagonalComboBox.getSelectedIndex());
		} else if (source == heuristicComboBox) {
			model.setSearchHeuristic(heuristicComboBox.getSelectedIndex());
		} else if (source == startButton) {
			model.startSearch();
		} else if (source == clearButton) {
			model.clearWalls();
		} else if (source == fitButton) {
			model.fit();
		} else if (source == mazeButton) {
			model.generateMaze();
		} else if (source == biCheckBox) {
			model.setBiSearch(biCheckBox.isSelected());
		}
		
	}
	
	// ==== ChangeListener Implementaiton ====

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == delaySlider) {
			Search.sleepTime = delaySlider.getValue();
		}		
	}

}
