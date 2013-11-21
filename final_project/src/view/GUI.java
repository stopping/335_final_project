package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import shared.Game;
import shared.GameSquare;
import shared.Unit;
import shared.Game.WinCondition;

public class GUI {
	
	private Game game;
	
	JFrame mainFrame = new JFrame();
	JPanel mainPanel = new JPanel();
	GamePanel gamePanel = new GamePanel();
	JPanel shopPanel = new JPanel();
	
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
		
		mainPanel.add(gamePanel);
		gamePanel.setPreferredSize(new Dimension(384,384));
		gamePanel.addMouseListener(new gameMouseListener());
		gamePanel.setBackground(Color.cyan);
		
		mainFrame.setResizable(false);
		mainFrame.add(mainPanel);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(800,600);
		mainFrame.setVisible(true);
		
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
			} else if(arg0.getButton() == MouseEvent.BUTTON3) {
				rightClickRow = arg0.getPoint().y / 32;
				rightClickCol = arg0.getPoint().x / 32;
			}
			
			gamePanel.repaint();
			System.out.println(arg0.getPoint().toString());
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class GamePanel extends JPanel {
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			GameSquare[][] map = game.getBoard();
			
			int size = 32;
			
			Random rng = new Random();
			
			for(int r = 0; r < map.length; r++) {
				for(int c = 0; c < map[0].length; c++) {
					int upper = r*size;
					int left = c*size;
					Rectangle2D square = new Rectangle2D.Double( left, upper, size, size );
					g2.draw(square);
					g2.setColor( ( r + c ) % 2 == 0 ? Color.black : Color.white );
					if(leftClickRow == r && leftClickCol == c ) g2.setColor(Color.yellow);
					if(rightClickRow == r && rightClickCol == c ) g2.setColor(Color.orange);
					g2.fill(square);
					
					int num1 = rng.nextInt(5)*size;
					int num2 = rng.nextInt(2)*size;
					g2.drawImage(sprites, left, upper, left+size, upper+size, num2, num1, num2+size, num1+size, null);
				}
			}
		}
	}
	
}
