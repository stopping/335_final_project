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
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import commands.*;
import shared.Game;
import shared.GameSquare;
import shared.Obstacle;
import shared.Unit;
import shared.Game.WinCondition;

public class GUI {
	
	private Game game;
	boolean selected = false;
	
	JFrame mainFrame = new JFrame();
	JPanel mainPanel = new JPanel();
	BoardPanel boardPanel = new BoardPanel();
	JPanel gamePanel = new JPanel();
	JPanel shopPanel = new JPanel();
	JTextArea gameInfo = new JTextArea();
	
	JButton endTurnButton = new JButton("End Turn");
	
	int leftClickRow;
	int leftClickCol;
	int rightClickRow;
	int rightClickCol;
	
	Image sprites;
	
	public GUI() {
		
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
		
		gameInfo.setPreferredSize(new Dimension(384,100));
		gameInfo.setEditable(false);

		boardPanel.setPreferredSize(new Dimension(384,384));
		boardPanel.addMouseListener(new gameMouseListener());
		boardPanel.setBackground(Color.cyan);
		
		gamePanel.setPreferredSize(new Dimension(400,550));
		gamePanel.add(boardPanel);
		gamePanel.add(gameInfo);
		gamePanel.add(endTurnButton);
		
		mainPanel.add(gamePanel);
		
		mainFrame.setResizable(false);
		mainFrame.add(mainPanel);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(800,600);
		mainFrame.setVisible(true);
		
		endTurnButton.addActionListener(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				GameCommand c = new EndTurnCommand();
				game.executeCommand(c);
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
			// TODO Auto-generated method stub
			
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
				gameInfo.setText(game.getBoard()[leftClickRow][leftClickCol].getOccupant().toString());
			} else if(arg0.getButton() == MouseEvent.BUTTON3) {
				rightClickRow = arg0.getPoint().y / 32;
				rightClickCol = arg0.getPoint().x / 32;
				if(selected) {
					int[] src = {leftClickRow,leftClickCol};
					int[] dest = {rightClickRow,rightClickCol};
					
					GameCommand c;
					if(game.getBoard()[rightClickRow][rightClickCol].hasOccupant()) c = new AttackCommand(src,dest);
					else c = new MoveCommand(src,dest);
					
					System.out.println(game.executeCommand(c));
					selected = false;
				}
			}
			
			boardPanel.repaint();

		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
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
					
					
					if( (srcSquare.getOccupant() instanceof Unit) && game.lineOfSightExists(srcSquare, destSquare) && selected &&
							Math.pow((r-leftClickRow),2) + Math.pow((c-leftClickCol), 2) <= Math.pow(((Unit)srcSquare.getOccupant()).getActionPoints(), 2)) {
						square = new Rectangle2D.Double( left+6, upper+6, size-13, size-13 );
						g2.setColor( Color.white );
						g2.draw(square);
						g2.fill(square);
					}
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
