package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BoundedRangeModel;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import shared.GameSquare;
import shared.Item;
import shared.Obstacle;
import shared.Occupant;
import unit.MeleeUnit;
import unit.RocketUnit;
import unit.Unit;
import client.HumanPlayer;

import commands.AttackCommand;
import commands.ClientServerCommand;
import commands.ClientServerCommand.ClientServerCommandType;
import commands.EndTurnCommand;
import commands.GameCommand;
import commands.MoveCommand;
import commands.UseItemCommand;

public class GUI extends HumanPlayer {

	boolean selected = false;
	DefaultListModel<Item> itemListModel = new DefaultListModel<Item>();
	DefaultListModel<String> actionListModel = new DefaultListModel<String>();
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
	JList<String> actionList = new JList<String>(actionListModel);
	JPanel chatPanel = new JPanel();
	JTextField chatField = new JTextField();
	JTextArea chatArea = new JTextArea();
	JScrollPane chatScrollPane = new JScrollPane(chatArea);

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

	public static final String GUI_OBJ_LOC = baseDir + "GUI.object";

	public GUI() {

		super();
		// sendCommand(new ClientServerCommand(ClientServerCommandType.Login,
		// new String[] {"Username","password"}));
		// sendCommand(new
		// ClientServerCommand(ClientServerCommandType.ResumeSession, null));

		// sendCommand(new ClientServerCommand(ClientServerCommandType.NewUser,
		// new String[] {"Username","password"}));
		// sendCommand(new ClientServerCommand(ClientServerCommandType.NewGame,
		// null));
		// sendCommand(new ClientServerCommand(ClientServerCommandType.NewUnit,
		// new String[] {"Alice", "Rocket"}));
		// sendCommand(new ClientServerCommand(ClientServerCommandType.NewUnit,
		// new String[] {"Bob", "Melee"}));
		// sendCommand(new ClientServerCommand(ClientServerCommandType.NewUnit,
		// new String[] {"Charles", "Melee"}));
		// sendCommand(new ClientServerCommand(ClientServerCommandType.NewUnit,
		// new String[] {"Dan", "Melee"}));
		// sendCommand(new ClientServerCommand(ClientServerCommandType.NewUnit,
		// new String[] {"Eric", "Melee"}));
		// sendCommand(new
		// ClientServerCommand(ClientServerCommandType.NewComputerPlayer, new
		// String[] {"1"}));
		// sendCommand(new
		// ClientServerCommand(ClientServerCommandType.StartGame, new String[]
		// {"CTF"}));
		//
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
		actionList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				if (actionList.getSelectedIndex() == -1)
					return;

				int[] src = { leftClickRow, leftClickCol };
				int[] dest = { rightClickRow, rightClickCol };

				GameCommand c;
				switch (actionList.getSelectedValue()) {
				case "Attack":
					c = new AttackCommand(src, dest);
					break;
				case "Move":
					c = new MoveCommand(src, dest);
					break;
				case "Cancel":
					c = null;
					break;
				default:
					c = null;
					break;
				}

				// System.out.println(game.executeCommand(c));
				if (c != null)
					sendCommand(c);
				// parseAndExecuteCommand(c);
				selected = false;
				actionList.setSelectedIndex(-1);
				actionListModel.removeAllElements();
				boardPanel.remove(actionList);
				update();

			}

		});

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
		newAccountButton.addActionListener(new AccountPanelListener());
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

		mainFrame.setResizable(false);
		mainFrame.add(mainPanel);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(800, 800);
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
				if (index >= 0) {
					int[] src = { leftClickRow, leftClickCol };
					UseItemCommand c = new UseItemCommand(src, src, index);
					sendCommand(c);
					itemListModel.removeElementAt(index);
				}
			}

		});

		chatScrollPane.getVerticalScrollBar().addAdjustmentListener(
				new ScrollBarListener());
		chatField.addKeyListener(new ChatFieldListener());
		mainFrame.addWindowListener(new WindowClosingListener());
	}

	public void login() {
		setUpLobbyPanel();
	}

	private void setUpUnitLists() {
		possibleUnitList.setPreferredSize(new Dimension(200, 87));
		userUnitList.setPreferredSize(new Dimension(200, 87));
		possibleUnitListModel.addElement(new MeleeUnit("Melee unit"));
		possibleUnitListModel.addElement(new RocketUnit("Rocket unit"));
	}

	public class AddUnitListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (userUnitListModel.getSize() < 5
					&& possibleUnitList.getSelectedValue() != null)
				userUnitListModel.addElement(possibleUnitList
						.getSelectedValue());
		}

	}

	public class RemoveUnitListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			userUnitListModel.removeElement(userUnitList.getSelectedValue());
		}

	}

	public class BeginAIGameListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (userUnitListModel.getSize() == 5) {
				ClientServerCommand com = null;

				sendCommand(new ClientServerCommand(
						ClientServerCommandType.NewGame, null));

				for (int i = 0; i < userUnitListModel.getSize(); i++) {
					if (userUnitListModel.get(i) instanceof MeleeUnit) {
						com = new ClientServerCommand(
								ClientServerCommandType.NewUnit, new String[] {
										"Unit " + i, "Melee" });
					}
					if (userUnitListModel.get(i) instanceof RocketUnit) {
						com = new ClientServerCommand(
								ClientServerCommandType.NewUnit, new String[] {
										"Unit " + i, "Rocket" });
					}
					sendCommand(com);
				}
				sendCommand(new ClientServerCommand(
						ClientServerCommandType.NewComputerPlayer,
						new String[] { "1" }));
				sendCommand(new ClientServerCommand(
						ClientServerCommandType.StartGame,
						new String[] { "CTF" }));
				mainPanel.removeAll();
				mainPanel.add(gamePanel);
				mainFrame.repaint();
			}
		}

	}

	private void setUpLobbyPanel() {
		JPanel aiGamePanel = new JPanel();
		JButton aiGameButton = new JButton("Begin AI game");
		aiGameButton.addActionListener(new BeginAIGameListener());
		JButton addUnitButton = new JButton("Add selected unit");
		JButton removeUnitButton = new JButton("Remove selected unit");
		JPanel selectUnitsPanel = new JPanel();

		lobbyPanel.setLayout(new BorderLayout());

		addUnitButton.addActionListener(new AddUnitListener());
		removeUnitButton.addActionListener(new RemoveUnitListener());

		JPanel possibleUnitsPanel = new JPanel();
		// possibleUnitsPanel.setLayout(new BoxLayout(possibleUnitsPanel,
		// BoxLayout.Y_AXIS));
		possibleUnitsPanel.setPreferredSize(new Dimension(200, 500));

		JPanel addUnitsPanel = new JPanel();
		// addUnitsPanel.setLayout(new BoxLayout(addUnitsPanel,
		// BoxLayout.Y_AXIS));
		addUnitsPanel.setPreferredSize(new Dimension(200, 500));

		JLabel possibleUnits = new JLabel("Select from here");
		JLabel yourUnits = new JLabel("Your current units");

		possibleUnitsPanel.add(possibleUnits);
		possibleUnitsPanel.add(possibleUnitList);
		possibleUnitsPanel.add(addUnitButton);

		addUnitsPanel.add(yourUnits);
		addUnitsPanel.add(userUnitList);
		addUnitsPanel.add(removeUnitButton);

		aiGamePanel.setPreferredSize(new Dimension(200, 400));
		selectUnitsPanel.setPreferredSize(new Dimension(600, 600));
		selectUnitsPanel.add(possibleUnitsPanel);
		selectUnitsPanel.add(addUnitsPanel);

		setUpUnitLists();
		aiGamePanel.add(aiGameButton);
		lobbyPanel.add(aiGamePanel, BorderLayout.SOUTH);
		lobbyPanel.add(selectUnitsPanel, BorderLayout.CENTER);
		mainPanel.removeAll();
		mainPanel.add(lobbyPanel);
		mainFrame.repaint();
		mainFrame.revalidate();

	}

	private void setUpNewAccountPanel() {
		// new account panel stuff
		usernameHere.setText("New username");
		passwordHere.setText("New password");
		accountPanel.setPreferredSize(new Dimension(250, 200));
		accountPanel.add(usernameHere);
		accountPanel.add(username);
		accountPanel.add(passwordHere);
		accountPanel.add(password);
		accountPanel.add(createButton);
		createButton.addActionListener(new CreateAccountListener());
		mainPanel.add(accountPanel);
	}

	public static void main(String[] args) {
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

			if (arg0.getButton() == MouseEvent.BUTTON1) {
				leftClickRow = arg0.getPoint().y / 32;
				leftClickCol = arg0.getPoint().x / 32;
				selected = true;
			} else if (arg0.getButton() == MouseEvent.BUTTON3) {
				rightClickRow = arg0.getPoint().y / 32;
				rightClickCol = arg0.getPoint().x / 32;
				if (selected) {

					GameSquare gs = game.getGameSquareAt(leftClickRow,
							leftClickCol);
					Unit performer = (Unit) gs.getOccupant();

					boardPanel.add(actionList);
					actionList.setLocation(rightClickCol * 32,
							rightClickRow * 32);
					actionListModel.removeAllElements();
					if (performer.canAttack(rightClickRow, rightClickCol))
						actionListModel.addElement("Attack");
					if (performer.canMoveTo(rightClickRow, rightClickCol))
						actionListModel.addElement("Move");
					actionListModel.addElement("Cancel");

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
			ClientServerCommand com = new ClientServerCommand(
					ClientServerCommandType.NewUser, new String[] {
							username.getText(), password });
			sendCommand(com);
			setUpLobbyPanel();
		}

	}

	private class AccountPanelListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			mainPanel.removeAll();
			setUpNewAccountPanel();
			mainFrame.repaint();
			mainFrame.revalidate();
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

			ClientServerCommand attemptToLog = new ClientServerCommand(
					ClientServerCommandType.Login, new String[] { name, pword });
			sendCommand(attemptToLog);
		}

	}

	private class WindowClosingListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			switch (JOptionPane.showConfirmDialog(null, "Save Session?")) {
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

	private class ChatFieldListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				String message = chatField.getText();
				sendCommand(new ClientServerCommand(
						ClientServerCommandType.Message,
						new String[] { message }));
			}
		}

		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
		}
	}

	public void update() {
		GameSquare gs = game.getBoard()[leftClickRow][leftClickCol];
		if (gs.hasOccupant()) {
			Occupant o = gs.getOccupant();
			gameInfo.setText(o.toString());
			itemListModel.removeAllElements();
			if (o instanceof Unit) {
				List<Item> list = ((Unit) o).getItemList();
				for (Item i : list) {
					itemListModel.addElement(i);
				}
			}

		} else
			gameInfo.setText("Empty");
		boardPanel.repaint();
	}

	public void failLogin(ClientServerCommand com) {
		String msg = com.getData().get(0);
		tryNewUser.setText(msg + "\n");
		tryNewUser.append("New user? Make a new account");
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

			for (int r = 0; r < map.length; r++) {
				for (int c = 0; c < map[0].length; c++) {
					int upper = r * size;
					int left = c * size;
					Rectangle2D square = new Rectangle2D.Double(left, upper,
							size, size);
					g2.draw(square);
					g2.setColor(map[r][c].getOccupant() instanceof Obstacle ? Color.black
							: Color.gray);
					if (leftClickRow == r && leftClickCol == c && selected)
						g2.setColor(Color.yellow);
					g2.fill(square);

					GameSquare srcSquare = game.getGameSquareAt(leftClickRow,
							leftClickCol);

					if (selected && srcSquare.getOccupant() instanceof Unit) {
						Unit u = (Unit) srcSquare.getOccupant();
						if (u.canMoveTo(r, c))
							g2.setColor(Color.white);
						if (u.canAttack(r, c))
							g2.setColor(Color.orange);
						square = new Rectangle2D.Double(left + 6, upper + 6,
								size - 13, size - 13);
						g2.draw(square);
						g2.fill(square);
					}
					/*
					 * if( (srcSquare.getOccupant() instanceof Unit) &&
					 * game.lineOfSightExists(srcSquare, destSquare) && selected
					 * && Math.pow((r-leftClickRow),2) +
					 * Math.pow((c-leftClickCol), 2) <=
					 * Math.pow(((Unit)srcSquare
					 * .getOccupant()).getActionPoints(), 2)) { square = new
					 * Rectangle2D.Double( left+6, upper+6, size-13, size-13 );
					 * g2.setColor( Color.white ); g2.draw(square);
					 * g2.fill(square); }
					 */
				}
			}

			for (Unit u : game.getBlueUnitList()) {
				if (u.isDead())
					continue;
				int upper = u.getLocation().getRow() * size;
				int left = u.getLocation().getCol() * size;
				int imagey = 0;
				if (u instanceof RocketUnit)
					imagey = size * 3;
				g2.drawImage(sprites, left, upper, left + size, upper + size,
						0, imagey, size, imagey + size, null);
			}

			for (Unit u : game.getRedUnitList()) {
				if (u.isDead())
					continue;
				int upper = u.getLocation().getRow() * size;
				int left = u.getLocation().getCol() * size;
				int imagey = 0;
				if (u instanceof RocketUnit)
					imagey = size * 3;
				g2.drawImage(sprites, left, upper, left + size, upper + size,
						size, imagey, size + size, imagey + size, null);
			}
		}
	}

}
