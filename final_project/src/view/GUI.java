package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import client.HumanPlayer;
import commands.*;
import commands.ClientServerCommand.ClientServerCommandType;
import shared.GameSquare;
import shared.Item;
import shared.Obstacle;
import shared.Occupant;
import unit.*;

public class GUI extends HumanPlayer {
	
	boolean selected = false;
	DefaultListModel<Item> itemListModel = new DefaultListModel<Item>();
	
	JFrame mainFrame = new JFrame();
	JPanel mainPanel = new JPanel();
	BoardPanel boardPanel = new BoardPanel();
	JPanel gamePanel = new JPanel();
	JPanel shopPanel = new JPanel();
	JTabbedPane lowerPane = new JTabbedPane();
	JTextArea gameInfo = new JTextArea();
	JList<Item> itemList = new JList<Item>(itemListModel);
	JPanel chatPanel = new JPanel();
	JTextField chatField = new JTextField();
	JTextArea chatArea = new JTextArea();
	JScrollPane chatScrollPane = new JScrollPane(chatArea);
	
	JPopupMenu actionMenu = new JPopupMenu("Action");
	JMenuItem moveItem = new JMenuItem("Move");
	JMenuItem attackItem = new JMenuItem("Attack");
	JMenuItem specialItem = new JMenuItem("Special");
	JMenuItem cancelItem = new JMenuItem("Cancel");
	
	JButton endTurnButton = new JButton("End Turn");
	JButton useItemButton = new JButton("Use Item");
	
	int leftClickRow;
	int leftClickCol;
	int rightClickRow;
	int rightClickCol;
	int mouseOverRow;
	int mouseOverCol;
	
	Image sprites;
	
	public static String baseDir = System.getProperty("user.dir")
			+ System.getProperty("file.separator");
	
	public static final String GUI_OBJ_LOC = baseDir
	        + "GUI.object";
	
	public GUI() {
		
		super();
//		sendCommand(new ClientServerCommand(ClientServerCommandType.Login, new String[] {"Username","password"}));
//		sendCommand(new ClientServerCommand(ClientServerCommandType.ResumeSession, null));

		sendCommand(new ClientServerCommand(ClientServerCommandType.NewUser, new String[] {"Username","password"}));
		sendCommand(new ClientServerCommand(ClientServerCommandType.NewGame, null));
		sendCommand(new ClientServerCommand(ClientServerCommandType.NewUnit, new String[] {"Alice", "Rocket"}));	
		sendCommand(new ClientServerCommand(ClientServerCommandType.NewUnit, new String[] {"Bob", "Melee"}));		
		sendCommand(new ClientServerCommand(ClientServerCommandType.NewUnit, new String[] {"Charles", "Melee"}));		
		sendCommand(new ClientServerCommand(ClientServerCommandType.NewUnit, new String[] {"Dan", "Melee"}));		
		sendCommand(new ClientServerCommand(ClientServerCommandType.NewUnit, new String[] {"Eric", "Melee"}));		
		sendCommand(new ClientServerCommand(ClientServerCommandType.NewComputerPlayer, new String[] {"1"}));
		sendCommand(new ClientServerCommand(ClientServerCommandType.StartGame, new String[] {"CTF"}));
		
		try {
			sprites = ImageIO.read(new File("Sprites2.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		lowerPane.setPreferredSize(new Dimension(384, 130));
		gameInfo.setEditable(false);
		
		actionMenu.setVisible(false);

		boardPanel.setPreferredSize(new Dimension(384, 384));
		boardPanel.addMouseListener(new gameMouseListener());
		boardPanel.setBackground(Color.cyan);
		boardPanel.setLayout(null);
		boardPanel.add(actionMenu);
		
		itemList.setPreferredSize(new Dimension(384, 80));
		
		gamePanel.setPreferredSize(new Dimension(400, 700));
		gamePanel.add(boardPanel);
		
		lowerPane.add("Info", gameInfo);
		chatPanel.setLayout(new BorderLayout());
		chatPanel.add(chatScrollPane, BorderLayout.CENTER);
		chatScrollPane.setVerticalScrollBar(new JScrollBar());
		chatArea.setEditable(false);
		chatPanel.add(chatField, BorderLayout.SOUTH);
		lowerPane.add("Chat", chatPanel);
		
		gamePanel.add(lowerPane);
		gamePanel.add(endTurnButton);
		gamePanel.add(itemList);
		gamePanel.add(useItemButton);
		
		mainPanel.add(gamePanel);
		
		mainFrame.setResizable(false);
		mainFrame.add(mainPanel);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(800,800);
		mainFrame.setVisible(true);
		
		setListeners();
	}
	
	public void setListeners() {
		
		attackItem.addActionListener(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				int[] src = {leftClickRow,leftClickCol};
				int[] dest = {rightClickRow,rightClickCol};
				
				Command com = new AttackCommand(src,dest);
				sendCommand(com);
				
				selected = false;
				actionMenu.setVisible(false);
				update();
				
			}
			
		});
		
		moveItem.addActionListener(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				int[] src = {leftClickRow,leftClickCol};
				int[] dest = {rightClickRow,rightClickCol};
				
				Command com = new MoveCommand(src,dest);
				sendCommand(com);
				
				selected = false;
				actionMenu.setVisible(false);
				update();
				
			}
			
		});
		
		cancelItem.addActionListener(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				selected = false;
				actionMenu.setVisible(false);
				update();
				
			}
			
		});
		
		endTurnButton.addActionListener(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				sendCommand(new EndTurnCommand());
			}
			
		});
		
		useItemButton.addActionListener(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int index = itemList.getSelectedIndex();
				if(index >= 0) {
					int[] src = {leftClickRow,leftClickCol};
					UseItemCommand c = new UseItemCommand(src,src,index);
					sendCommand(c);
					itemListModel.removeElementAt(index);
				}
			}
			
		});
		
		chatScrollPane.getVerticalScrollBar().addAdjustmentListener(new ScrollBarListener());
		chatField.addKeyListener(new ChatFieldListener());
		mainFrame.addWindowListener(new WindowClosingListener());
	}
	
	public static void main( String[] args ) {
		new GUI();
	}
	
	private class gameMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			
			if(arg0.getButton() == MouseEvent.BUTTON1) {
				leftClickRow = arg0.getPoint().y / 32;
				leftClickCol = arg0.getPoint().x / 32;
				selected = true;
			} else if(arg0.getButton() == MouseEvent.BUTTON3) {
				rightClickRow = arg0.getPoint().y / 32;
				rightClickCol = arg0.getPoint().x / 32;
				if(selected) {

					GameSquare gs = game.getGameSquareAt(leftClickRow, leftClickCol);
					if(!gs.hasOccupant() || !(gs.getOccupant() instanceof Unit)) return;
					
					Unit performer = (Unit) gs.getOccupant();
					
					actionMenu.removeAll();
					
					if(performer.canAttack(rightClickRow, rightClickCol)) actionMenu.add(attackItem);
					if(performer.canMoveTo(rightClickRow, rightClickCol)) actionMenu.add(moveItem);
					actionMenu.add(cancelItem);
					
					actionMenu.show(arg0.getComponent(), rightClickCol*32+16, rightClickRow*32+16);

				}
			}
			
			update();

		}

		@Override
		public void mouseReleased(MouseEvent arg0) {

		}
		
	}
	
	private class WindowClosingListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			switch(JOptionPane.showConfirmDialog(null, "Save Session?")) {
		  		case JOptionPane.YES_OPTION:
		  			sendCommand(new ClientServerCommand(
		  					ClientServerCommandType.SuspendSession, null));
		  			System.exit(0);
		  		case JOptionPane.NO_OPTION:
		  			System.exit(0);
			}
		} 
	}
	
	private class ScrollBarListener implements AdjustmentListener {

		BoundedRangeModel brm = chatScrollPane.getVerticalScrollBar().getModel();
	   boolean wasAtBottom = true;

	   public void adjustmentValueChanged(AdjustmentEvent e) {
	   	if (!brm.getValueIsAdjusting()) {
	   		if (wasAtBottom)
	   			brm.setValue(brm.getMaximum());
	      } else
	      	wasAtBottom = ((brm.getValue() + brm.getExtent()) == brm.getMaximum());
	   }
	}
	
	
	private class ChatFieldListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				String message = chatField.getText();
				sendCommand(new ClientServerCommand(
						ClientServerCommandType.Message, new String[] {message}));
				chatField.setText("");
			}
		}
		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
	}
	
	
	public void update() {
		GameSquare gs = game.getBoard()[leftClickRow][leftClickCol];
		if(gs.hasOccupant()) {
			Occupant o = gs.getOccupant();
			gameInfo.setText(o.toString());
			itemListModel.removeAllElements();
			if(o instanceof Unit) {
				List<Item> list = ((Unit) o).getItemList();
				for(Item i : list) {
					itemListModel.addElement(i);
				}
			}
			
		}
		else gameInfo.setText("Empty");
		mainPanel.repaint();
	}
	
	public void receiveMessage(ClientServerCommand com) {
		String source = com.getData().get(0);
		String msg = com.getData().get(2);
		chatArea.append(source + ": " + msg + "\n");
	}
	
	private class BoardPanel extends JPanel {
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			GameSquare[][] map = game.getBoard();
			
			int size = 32;
			
			for(int r = 0; r < map.length; r++) {
				for(int c = 0; c < map[0].length; c++) {
					int upper = r*size;
					int left = c*size;
					Rectangle2D square = new Rectangle2D.Double( left, upper, size, size );
					g2.draw(square);
					g2.setColor( map[r][c].getOccupant() instanceof Obstacle ? Color.black : Color.gray );
					if(leftClickRow == r && leftClickCol == c && selected ) g2.setColor(Color.yellow);
					g2.fill(square);
					
					GameSquare srcSquare = game.getGameSquareAt(leftClickRow,leftClickCol);
					
					if( selected && srcSquare.getOccupant() instanceof Unit) {
						Unit u = (Unit) srcSquare.getOccupant();
						if( u.canMoveTo(r,c) ) g2.setColor( Color.white );
						if( u.canAttack(r,c)) g2.setColor( Color.orange );
						square = new Rectangle2D.Double( left+6, upper+6, size-13, size-13 );
						g2.draw(square);
						g2.fill(square);
					}
				}
			}
			
			for(Unit u : game.getBlueUnitList()) {
				if(u.isDead()) continue;
				int upper = u.getLocation().getRow()*size;
				int left = u.getLocation().getCol()*size;
				int imagey = 0;
				if(u instanceof RocketUnit) imagey = size*3;
				if(u instanceof MeleeUnit) imagey = size*4;
				g2.drawImage(sprites, left, upper, left+size, upper+size, 0, imagey, size, imagey+size, null);
			}
			
			for(Unit u : game.getRedUnitList()) {
				if(u.isDead()) continue;
				int upper = u.getLocation().getRow()*size;
				int left = u.getLocation().getCol()*size;
				int imagey = 0;
				if(u instanceof RocketUnit) imagey = size*3;
				if(u instanceof MeleeUnit) imagey = size*4;
				g2.drawImage(sprites, left, upper, left+size, upper+size, size, imagey, size+size, imagey+size, null);
			}
		}
	}
	
}
