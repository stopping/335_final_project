package view;

import game_commands.AttackCommand;
import game_commands.EndTurnCommand;
import game_commands.MoveCommand;
import game_commands.UseAbilityCommand;
import game_commands.UseItemCommand;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import shared.Command;
import shared.Game.WinCondition;
import shared.GameSquare;
import shared.Item;
import shared.MapBehavior;
import shared.Obstacle;
import shared.ObstacleMap;
import shared.Occupant;
import shared.StandardMap;
import unit.DemolitionUnit;
import unit.EngineerUnit;
import unit.MeleeUnit;
import unit.RocketUnit;
import unit.SoldierUnit;
import unit.Unit;
import unit.Unit.UnitClass;
import client.HumanPlayer;
import client_commands.ComputerDifficultySet;
import client_commands.JoinGame;
import client_commands.Login;
import client_commands.Logout;
import client_commands.NewComputerPlayer;
import client_commands.NewGame;
import client_commands.NewUnit;
import client_commands.NewUser;
import client_commands.PlayerMessage;
import client_commands.PlayerReady;
import client_commands.RequestGameRooms;
import client_commands.StartGame;
import client_commands.SuspendSession;


public class GUI extends HumanPlayer {

	boolean selected = false;
	DefaultListModel<Item> itemListModel = new DefaultListModel<Item>();
	DefaultListModel<Unit> possibleUnitListModel = new DefaultListModel<Unit>();
	DefaultListModel<Unit> userUnitListModel = new DefaultListModel<Unit>();
	JList<Unit> possibleUnitList = new JList<Unit>(possibleUnitListModel);
	JList<Unit> userUnitList = new JList<Unit>(userUnitListModel);

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
	
	JPanel mainOptionsPanel = new JPanel();
	JLabel welcomeLabel = new JLabel();
	JButton multiplayerButton = new JButton("Multiplayer");
	JButton singleplayerButton = new JButton("Singleplayer");
	JButton resumeSessionButton = new JButton("ResumeSession");
	JButton accountInfoButton = new JButton("Account Info");
	JButton logoutButton = new JButton("Logout");
	JPanel loadoutPanel = new JPanel();
	String gameTypes[] = new String[] { "Deathmatch", "Demolition", "CTF"};
	JComboBox<String> gameTypeComboBox = new JComboBox<String>(gameTypes);
	JPanel startGameButs = new JPanel();
	
	JPopupMenu actionMenu = new JPopupMenu("Action");
	JMenuItem moveItem = new JMenuItem("Move");
	JMenuItem attackItem = new JMenuItem("Attack");
	JMenuItem specialItem = new JMenuItem("Special");
	JMenuItem cancelItem = new JMenuItem("Cancel");

	JPanel lobbyPanel = new JPanel();
	JPanel failedLoginPanel = new JPanel();
	JTextArea tryNewUser = new JTextArea("New user? Make a new account");
	JButton newAccountButton = new JButton("Create new account");
	JButton createButton = new JButton("Create");
	JPanel accountPanel = new JPanel();
	JTextField username = new JTextField(10);
	JPasswordField password = new JPasswordField(10);
	JPanel loginPanel = new JPanel();
	JTextArea usernameHere = new JTextArea("Username");
	JTextArea passwordHere = new JTextArea("Password");
	JButton loginButton = new JButton("Login");
	JPanel logisticsPanel = new JPanel();
	JPanel loginButtonPanel = new JPanel();
	
	JButton gameRoomLobbyButton = new JButton("Go To Lobby");
	JPanel gameRoomLobbyPanel = new JPanel();
	JTable gameRoomsTable = new JTable();
	JButton newGameRoomButton = new JButton("New GameRoom");
	JLabel gameRoomLobbyLabel = new JLabel("Open GameRooms");
	JButton joinGameRoomButton = new JButton("Join");
	JButton startGameButton = new JButton("Start Game!");
	JPanel startGameOptionsPanel = new JPanel();
	
	JLabel AILabel = new JLabel("AI level: ");
	String AIDifficultyLevels[] = new String[] { "Friendly", "Normal", "Destructive", "Insane"};
	final JComboBox<String> AILevelComboBox = new JComboBox<String>(AIDifficultyLevels);
	
	String maps[] = new String[] { "Standard", "Obstacle"};
	final JComboBox<String> mapTypeComboBox = new JComboBox<String>(maps);

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

	public GUI() {

		super();
		
		try {
			sprites = ImageIO.read(new File("Sprites2.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		lowerPane.setPreferredSize(new Dimension(384, 130));
		gameInfo.setEditable(false);

		boardPanel.setPreferredSize(new Dimension(384, 384));
		boardPanel.addMouseListener(new gameMouseListener());
		boardPanel.setBackground(Color.cyan);

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

		// login panel
		newAccountButton.addActionListener(new CreateAccountListener());
		failedLoginPanel.add(tryNewUser);
		failedLoginPanel.add(newAccountButton);
		loginButton.addActionListener(new LoginListener());
		loginPanel.setLayout(new GridLayout(2, 2));
		usernameHere.setEditable(false);
		passwordHere.setEditable(false);
		loginPanel.add(usernameHere);
		loginPanel.add(username);
		loginPanel.add(passwordHere);
		loginPanel.add(password);
		logisticsPanel.setPreferredSize(new Dimension(350, 300));
		loginButtonPanel.add(loginButton);
		logisticsPanel.add(loginPanel);
		logisticsPanel.add(loginButtonPanel);
		logisticsPanel.add(failedLoginPanel);
		mainPanel.add(logisticsPanel);

		setuploadoutPanel();
		setupGameRoomLobby();
		setupMainOptionsPanel();
		
		mainFrame.setResizable(false);
		mainFrame.add(mainPanel);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(800, 800);
		mainFrame.setVisible(true);

		setListeners();
	}
	
	public static void main(String[] args) {
		new GUI();
	}

	@SuppressWarnings("serial")
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
		
		specialItem.addActionListener(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				int[] src = {leftClickRow,leftClickCol};
				int[] dest = {rightClickRow,rightClickCol};
				
				Command com = new UseAbilityCommand(src,dest);
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
		//mainFrame.addWindowListener(new WindowClosingListener());
	}
	
	public void login() {
		showPanel(mainOptionsPanel);
	}
	
	private void showPanel(JPanel p) {
		mainPanel.removeAll();
		mainPanel.add(p);
		mainFrame.repaint();
		mainFrame.revalidate();
	}
	
	@Override
	public void failLogin() {
		tryNewUser.setText(("Invalid. New user? Make a new account"));
	}
	
	private void setuploadoutPanel() {
		JButton addUnitButton = new JButton("Add selected unit");
		JButton removeUnitButton = new JButton("Remove selected unit");
		JPanel selectUnitsPanel = new JPanel();

		loadoutPanel.setLayout(new BorderLayout());

		addUnitButton.addActionListener(new AddUnitListener());
		removeUnitButton.addActionListener(new RemoveUnitListener());

		JPanel possibleUnitsPanel = new JPanel();
		possibleUnitsPanel.setPreferredSize(new Dimension(200, 500));

		JPanel addUnitsPanel = new JPanel();
		addUnitsPanel.setPreferredSize(new Dimension(200, 500));

		JLabel possibleUnits = new JLabel("Select from here");
		JLabel yourUnits = new JLabel("Your current units");

		possibleUnitsPanel.add(possibleUnits);
		possibleUnitsPanel.add(possibleUnitList);
		possibleUnitsPanel.add(addUnitButton);

		addUnitsPanel.add(yourUnits);
		addUnitsPanel.add(userUnitList);
		addUnitsPanel.add(removeUnitButton);

		selectUnitsPanel.setPreferredSize(new Dimension(600, 600));
		selectUnitsPanel.add(possibleUnitsPanel);
		selectUnitsPanel.add(addUnitsPanel);

		setUpUnitLists();
		loadoutPanel.add(selectUnitsPanel, BorderLayout.CENTER);
		JButton readyButton = new JButton("Ready");
		
		startGameOptionsPanel.setLayout(new GridLayout(4,2));
		startGameOptionsPanel.add(AILabel);
		startGameOptionsPanel.add(AILevelComboBox);
		startGameOptionsPanel.add(new JLabel("Win Condition: "));
		startGameOptionsPanel.add(gameTypeComboBox);
		startGameOptionsPanel.add(new JLabel("Map: "));
		startGameOptionsPanel.add(mapTypeComboBox);
		startGameOptionsPanel.add(readyButton);
		startGameButton.setEnabled(false);
		startGameOptionsPanel.add(startGameButton);
		loadoutPanel.add(startGameOptionsPanel, BorderLayout.SOUTH);
		
		readyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (userUnitListModel.getSize() == 5) {
					sendNewUnitCommands();
					sendCommand(new ComputerDifficultySet(AILevelComboBox.getSelectedIndex()));
					sendCommand(new PlayerReady());
					userUnitList.setEnabled(false);
				}
			}	
		});
		
		startGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					String gameType = (String) gameTypeComboBox.getSelectedItem();
					WinCondition cond = WinCondition.valueOf(gameType);
					MapBehavior map = null;
					switch (mapTypeComboBox.getSelectedIndex()) {
					case 0:
						map = new StandardMap();
						break;
					case 1:
						map = new ObstacleMap();
						break;
					}
					sendCommand(new StartGame(cond, map));
				}
			
		});
	}
	
	private void setupGameRoomLobby() {
		gameRoomLobbyPanel.setLayout(new GridLayout(2, 2));
		gameRoomLobbyPanel.add(newGameRoomButton);
		gameRoomLobbyPanel.add(joinGameRoomButton);
		gameRoomLobbyPanel.add(gameRoomLobbyLabel);
		gameRoomLobbyPanel.add(gameRoomsTable);

		newGameRoomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendCommand(new NewGame());
				showPanel(loadoutPanel);
			}
		});
		
		joinGameRoomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (gameRoomsTable.getSelectedRow() >= 0)
				sendCommand(new JoinGame(gameRoomsTable.getSelectedRow()));
				showPanel(loadoutPanel);
			}
		});
	}
	
	private void setupMainOptionsPanel() {
		welcomeLabel.setText("Welcome! You have " + credits + " credits.");
		
		mainOptionsPanel.setLayout(new GridLayout(6, 1));
		mainOptionsPanel.add(welcomeLabel);
		mainOptionsPanel.add(singleplayerButton);
		mainOptionsPanel.add(multiplayerButton);
		mainOptionsPanel.add(resumeSessionButton);
		mainOptionsPanel.add(accountInfoButton);
		mainOptionsPanel.add(logoutButton);
		
		singleplayerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendCommand(new NewGame());
				sendCommand(new NewComputerPlayer());
				showPanel(loadoutPanel);
			}
		});
		
		multiplayerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendCommand(new RequestGameRooms());
				mainPanel.removeAll();
				startGameOptionsPanel.remove(AILabel);
				startGameOptionsPanel.remove(AILevelComboBox);
				showPanel(gameRoomLobbyPanel);
			}
		});
		
		/*resumeSessionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// send command to request resume session
			}
		});*/
		
		 /* accountInfoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainPanel.removeAll();
				mainPanel.add(accountInfoPanel);
				mainFrame.repaint();
				mainFrame.revalidate();
			}
		}); */
		
		logoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendCommand(new Logout());
				showPanel(logisticsPanel);
			}
		});
	}
	
	private void setUpUnitLists() {
		possibleUnitList.setPreferredSize(new Dimension(200, 87));
		userUnitList.setPreferredSize(new Dimension(200, 87));
		possibleUnitListModel.addElement(new MeleeUnit("Melee unit"));
		possibleUnitListModel.addElement(new RocketUnit("Rocket unit"));
		possibleUnitListModel.addElement(new SoldierUnit("Soldier unit"));
		possibleUnitListModel.addElement(new EngineerUnit("Engineer unit"));
		possibleUnitListModel.addElement(new DemolitionUnit("Demolition unit"));
	}

	public class AddUnitListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			if (userUnitListModel.getSize() < 5 && possibleUnitList.getSelectedValue() != null)
				userUnitListModel.addElement(possibleUnitList.getSelectedValue());
		}
	}

	public class RemoveUnitListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			userUnitListModel.removeElement(userUnitList.getSelectedValue());
		}
	}
	
	private void sendNewUnitCommands() {
		for (int i = 0; i < userUnitListModel.getSize(); i++) {
			UnitClass uc = null;
			if (userUnitListModel.get(i) instanceof MeleeUnit) 
				uc = UnitClass.Melee;
			else if (userUnitListModel.get(i) instanceof RocketUnit) 
				uc = UnitClass.Rocket;			
			else if (userUnitListModel.get(i) instanceof SoldierUnit) 
				uc = UnitClass.Soldier;			
			else if (userUnitListModel.get(i) instanceof DemolitionUnit) 
				uc = UnitClass.Demolition;			
			else if (userUnitListModel.get(i) instanceof EngineerUnit) 
				uc = UnitClass.Engineer;			
			
			sendCommand(new NewUnit("unit", uc));
		}
	}
	
	@Override
	public void updateAvailGameRooms(HashMap<Integer, String> rooms) {
		gameRoomLobbyPanel.remove(gameRoomsTable);
		String[] columnnames = new String[] { "Room", "Player" };
		Object[][] data = new Object[rooms.size()][2];
		for (int i= 0 ; i < rooms.size() ; i++) {
			data[i][0] = new Integer(i);
			data[i][1] = rooms.get(i);
		}
		gameRoomsTable = new JTable(data, columnnames);
		gameRoomLobbyPanel.add(gameRoomsTable);
		gameRoomLobbyPanel.repaint();
		gameRoomLobbyPanel.revalidate();
		gameRoomLobbyPanel.updateUI();
		System.out.println("received open gamerooms");
	}
	
	@Override
	public void canStartGame() {
		startGameButton.setEnabled(true);
		startGameButs.repaint();
		startGameButs.revalidate();
		startGameButs.updateUI();
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
					if(performer.canUseAbility(rightClickRow, rightClickCol)) actionMenu.add(specialItem);
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

	private class CreateAccountListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			char[] temp = password.getPassword();
			String password = "";
			for (char c : temp) {
				password += c;
			}
			sendCommand(new NewUser(username.getText(), password));
		}
	}

	private class LoginListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String name = username.getText();
			char[] temp = password.getPassword();
			String pword = "";
			for (char c : temp) {
				pword += c;
			}
			sendCommand(new Login(name, pword));
		}
	}

	private class WindowClosingListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			switch (JOptionPane.showConfirmDialog(null, "Save Session?")) {
			case JOptionPane.YES_OPTION:
				sendCommand(new SuspendSession());
				System.exit(0);
				break;
			case JOptionPane.NO_OPTION:
				sendCommand(new Logout());
				System.exit(0);
				break;
			case JOptionPane.DEFAULT_OPTION:
				break;
			}
		}
	}

	private class ScrollBarListener implements AdjustmentListener {

		BoundedRangeModel brm = chatScrollPane.getVerticalScrollBar()
				.getModel();
		boolean wasAtBottom = true;

		public void adjustmentValueChanged(AdjustmentEvent e) {
			if (!brm.getValueIsAdjusting()) {
				if (wasAtBottom)
					brm.setValue(brm.getMaximum());
			} else
				wasAtBottom = ((brm.getValue() + brm.getExtent()) == brm
						.getMaximum());
		}
	}
	
	@Override
	public void receiveMessage(String source, String msg) {
		chatArea.append(source + ": " + msg + "\n");
	}

	private class ChatFieldListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				String message = chatField.getText();
				sendCommand(new PlayerMessage(message));
				chatField.setText("");
			}
		}

		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
		}
	}
	
	@Override
	public void showGamePanel() {
		mainPanel.removeAll();
		mainPanel.add(gamePanel);
		mainFrame.repaint();
	}

	public void update() {
		welcomeLabel.setText("Welcome! You have " + credits + " credits.");
		
		if(game != null) {
			GameSquare gs = game.getGameSquareAt(leftClickRow, leftClickCol);
			if(game.getWinner() != -1) {
				if(game.isCurrentPlayer(game.getWinner()))
					gameInfo.setText("You won!");
				else
					gameInfo.setText("You lost.");
			} else if (gs.hasOccupant()) {
				Occupant o = gs.getOccupant();
				gameInfo.setText(o.toString());
				itemListModel.removeAllElements();
				if (o instanceof Unit) {
					List<Item> list = ((Unit) o).getItemList();
					for (Item i : list) {
						itemListModel.addElement(i);
					}
				}
			} else {
				gameInfo.setText("Empty");
			}
			
			boardPanel.repaint();
		}
	}

	@SuppressWarnings("serial")
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
						if( u.canAttack(r,c)) {
							g2.setColor( Color.red );
							square = new Rectangle2D.Double( left+6, upper+6, size-13, size-13 );
							g2.draw(square);
							g2.fill(square);
						}
						if( u.canUseAbility(r,c)) {
							g2.setColor( Color.blue );
							square = new Rectangle2D.Double( left+6, upper+6, size-13, size-13 );
							g2.draw(square);
							g2.fill(square);
						}
						if( u.canMoveTo(r,c) ) {
							g2.setColor( Color.white );
							square = new Rectangle2D.Double( left+10, upper+10, size-21, size-21 );
							g2.draw(square);
							g2.fill(square);
						}
					}
				}
			}
			
			for(Unit u : game.getBlueUnitList()) {
				if(u.isDead()) continue;
				int upper = u.getLocation().getRow()*size;
				int left = u.getLocation().getCol()*size;
				int imagey = 0;
				if(u instanceof SoldierUnit) imagey = size*0;
				if(u instanceof EngineerUnit) imagey = size*1;
				if(u instanceof DemolitionUnit) imagey = size*2;
				if(u instanceof RocketUnit) imagey = size*3;
				if(u instanceof MeleeUnit) imagey = size*4;
				g2.drawImage(sprites, left, upper, left+size, upper+size, 0, imagey, size, imagey+size, null);
			}
			
			for(Unit u : game.getRedUnitList()) {
				if(u.isDead()) continue;
				int upper = u.getLocation().getRow()*size;
				int left = u.getLocation().getCol()*size;
				int imagey = 0;
				if(u instanceof SoldierUnit) imagey = size*0;
				if(u instanceof EngineerUnit) imagey = size*1;
				if(u instanceof DemolitionUnit) imagey = size*2;
				if(u instanceof RocketUnit) imagey = size*3;
				if(u instanceof MeleeUnit) imagey = size*4;
				g2.drawImage(sprites, left, upper, left+size, upper+size, size, imagey, size+size, imagey+size, null);
			}
		}
	}
}