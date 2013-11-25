package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import client.HumanPlayer;
import commands.*;
import commands.ClientServerCommand.ClientServerCommandType;
import shared.Game;
import shared.GameSquare;
import shared.Item;
import shared.Obstacle;
import shared.Game.WinCondition;
import shared.Occupant;
import unit.Unit;

public class GUI extends HumanPlayer {
	
	boolean selected = false;
	DefaultListModel<Item> itemListModel = new DefaultListModel<Item>();
	
	JFrame mainFrame = new JFrame();
	JPanel mainPanel = new JPanel();
	BoardPanel boardPanel = new BoardPanel();
	JPanel gamePanel = new JPanel();
	JPanel shopPanel = new JPanel();
	JTextArea gameInfo = new JTextArea();
	JList<Item> itemList = new JList<Item>(itemListModel);
	
	JButton endTurnButton = new JButton("End Turn");
	JButton useItemButton = new JButton("Use Item");
	
	int leftClickRow;
	int leftClickCol;
	int rightClickRow;
	int rightClickCol;
	int mouseOverRow;
	int mouseOverCol;
	
	Image sprites;
	
	public GUI() {
		
		super();
		
		sendCommand(new ClientServerCommand(ClientServerCommandType.Login, new String[] {"Username","password"}));
		sendCommand(new ClientServerCommand(ClientServerCommandType.NewComputerPlayer, null));
		sendCommand(new ClientServerCommand(ClientServerCommandType.StartGame, new String[] {"CTF"}));
		
		ArrayList<Unit> list1 = new ArrayList<Unit>();
		ArrayList<Unit> list2 = new ArrayList<Unit>();
		
		list1.add(new Unit("Alice"));
		list1.add(new Unit("Bob"));
		list1.add(new Unit("Charlie"));
		list1.add(new Unit("David"));
		list1.add(new Unit("Eric"));
		
		list2.add(new Unit("Zander"));
		list2.add(new Unit("Yvonne"));
		list2.add(new Unit("Xavier"));
		list2.add(new Unit("Will"));
		list2.add(new Unit("Van"));
		
		game = new Game(list1,list2,WinCondition.Deathmatch);
		
		try {
			sprites = ImageIO.read(new File("Sprites2.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		gameInfo.setPreferredSize(new Dimension(384, 100));
		gameInfo.setEditable(false);

		boardPanel.setPreferredSize(new Dimension(384, 384));
		boardPanel.addMouseListener(new gameMouseListener());
		boardPanel.setBackground(Color.cyan);
		
		itemList.setPreferredSize(new Dimension(384, 80));
		
		gamePanel.setPreferredSize(new Dimension(400, 700));
		gamePanel.add(boardPanel);
		gamePanel.add(gameInfo);
		gamePanel.add(endTurnButton);
		gamePanel.add(itemList);
		gamePanel.add(useItemButton);
		
		mainPanel.add(gamePanel);
		
		mainFrame.setResizable(false);
		mainFrame.add(mainPanel);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(800,800);
		mainFrame.setVisible(true);
		
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
					int[] src = {leftClickRow,leftClickCol};
					int[] dest = {rightClickRow,rightClickCol};
					
					GameCommand c;
					if(game.getBoard()[rightClickRow][rightClickCol].hasOccupant()) c = new AttackCommand(src,dest);
					else c = new MoveCommand(src,dest);
					
					//System.out.println(game.executeCommand(c));
					sendCommand(c);
					//parseAndExecuteCommand(c);
					selected = false;
				}
			}
			
			update();

		}

		@Override
		public void mouseReleased(MouseEvent arg0) {

		}
		
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
		boardPanel.repaint();
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
					GameSquare destSquare = game.getGameSquareAt(r, c);
					
					if( selected && srcSquare.getOccupant() instanceof Unit) {
						Unit u = (Unit) srcSquare.getOccupant();
						if( u.canMoveTo(destSquare) ) g2.setColor( Color.white );
						Occupant o = destSquare.getOccupant();
						if( o != null && u.canAttack(o)) g2.setColor( Color.orange );
						square = new Rectangle2D.Double( left+6, upper+6, size-13, size-13 );
						g2.draw(square);
						g2.fill(square);
					}
/*					if( (srcSquare.getOccupant() instanceof Unit) && game.lineOfSightExists(srcSquare, destSquare) && selected &&
							Math.pow((r-leftClickRow),2) + Math.pow((c-leftClickCol), 2) <= Math.pow(((Unit)srcSquare.getOccupant()).getActionPoints(), 2)) {
						square = new Rectangle2D.Double( left+6, upper+6, size-13, size-13 );
						g2.setColor( Color.white );
						g2.draw(square);
						g2.fill(square);
					}*/
				}
			}
			
			for(Unit u : game.getBlueUnitList()) {
				if(u.isDead()) continue;
				int upper = u.getLocation().getRow()*size;
				int left = u.getLocation().getCol()*size;
				g2.drawImage(sprites, left, upper, left+size, upper+size, 0, 0, size, size, null);
			}
			
			for(Unit u : game.getRedUnitList()) {
				if(u.isDead()) continue;
				int upper = u.getLocation().getRow()*size;
				int left = u.getLocation().getCol()*size;
				g2.drawImage(sprites, left, upper, left+size, upper+size, 32, 0, 32+size, size, null);
			}
		}
	}
	
}
