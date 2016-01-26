import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

public class MainWindow {

	private class tilestruct {
		public byte valuestatus, coverstatus;
		/*
		 * Legend:
		 * 
		 * valuestatus: -1 - mine tile; 0 - empty tile; 1-8 - non-mine tile with
		 * 1-8 adjacent mines; 9 - first clicked tile
		 * 
		 * coverstatus: -1 - opened status; 0 - default (empty) status; 1 -
		 * flagged status; 2 - unsure (?) status
		 */
		public JPanel panel;
		public JLabel value;
		public JButton cover;
	}
	private static int mines, closedsafetiles = 256, time = 0;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	private JFrame frame;
	private JLabel mines_left;
	private JButton new_game;
	private JLabel time_past;

	private JLabel mines_left_value;
	private JLabel time_past_value;
	private JPanel field;
	private final ImageIcon gamelost = new ImageIcon("images/gamelost.png");
	private final ImageIcon gamewon = new ImageIcon("images/gamewon.png");
	private final ImageIcon leftclick = new ImageIcon("images/leftclick.png");
	private final ImageIcon minesweeper = new ImageIcon("images/minesweeper.png");
	private final ImageIcon newgame = new ImageIcon("images/newgame.png");
	private final ImageIcon redmine_broken = new ImageIcon("images/redmine_broken.png");
	private final ImageIcon tile_flagged = new ImageIcon("images/tile_flagged.png");
	private final ImageIcon tile_mine_flag = new ImageIcon("images/tile_mine_flag.png");
	private final ImageIcon tile_mine_nomine = new ImageIcon("images/tile_mine_nomine.png");
	private final ImageIcon tile_mine = new ImageIcon("images/tile_mine.png");

	private final ImageIcon tile_unsure = new ImageIcon("images/tile_unsure.png");

	private final ImageIcon tile = new ImageIcon("images/tile.png");

	private Timer timer;

	private final tilestruct[][] tiles = new tilestruct[18][18];

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}
	private void choose_difficulty() {
		final DifficultyWindow dw = new DifficultyWindow();
		dw.setLocationRelativeTo(null);
		dw.setVisible(true);

		mines = dw.level();
		mines_left_value.setText(Integer.toString(mines));
		closedsafetiles -= mines;
	}

	// Generates the 16x16 game field
	private void create_new_field() {
		time_past_value.setText(Integer.toString(time));

		for (int i = 0; i < 18; i++) {
			tiles[i][0] = new tilestruct();
			tiles[i][0].coverstatus = -1;
			tiles[i][17] = new tilestruct();
			tiles[i][17].coverstatus = -1;
		}
		for (int j = 1; j < 17; j++) {
			tiles[0][j] = new tilestruct();
			tiles[0][j].coverstatus = -1;
			tiles[17][j] = new tilestruct();
			tiles[17][j].coverstatus = -1;
		}

		@SuppressWarnings("unused")
		int l = 0, t = 0;

		for (int i = 1; i < 17; i++) {
			for (int j = 1; j < 17; j++) {
				final int i2 = i;
				final int j2 = j;
				tiles[i][j] = new tilestruct();

				tiles[i][j].panel = new JPanel(new CardLayout());
				tiles[i][j].panel.setPreferredSize(new Dimension(32, 32));

				tiles[i][j].cover = new JButton();
				tiles[i][j].cover.setMargin(new Insets(0, 0, 0, 0));
				tiles[i][j].cover.setPreferredSize(new Dimension(32, 32));
				tiles[i][j].cover.setIcon(tile);

				tiles[i][j].cover.addMouseListener(new MouseAdapter() {
					// Executes events when a tile is pressed
					@Override
					public void mousePressed(MouseEvent e) {
						if (SwingUtilities.isLeftMouseButton(e)) {
							if (tiles[i2][j2].coverstatus != 1) {
								new_game.setIcon(leftclick);

								if (!timer.isRunning()) {
									tiles[i2][j2].valuestatus = 9;
									create_new_mines();
								}
							}
						} else if (SwingUtilities.isRightMouseButton(e)) {
							if (tiles[i2][j2].coverstatus == 0) {
								if (mines > 0) {
									tiles[i2][j2].cover.setIcon(tile_flagged);
									tiles[i2][j2].coverstatus = 1;

									mines--;
									mines_left_value.setText(Integer.toString(mines));
								}
							} else if (tiles[i2][j2].coverstatus == 1) {
								tiles[i2][j2].cover.setIcon(tile_unsure);
								tiles[i2][j2].coverstatus = 2;

								mines++;
								mines_left_value.setText(Integer.toString(mines));
							} else {
								tiles[i2][j2].cover.setIcon(tile);
								tiles[i2][j2].coverstatus = 0;
							}
						}
					}

					// Executes events when a tile is released
					@Override
					public void mouseReleased(MouseEvent e) {
						if (SwingUtilities.isLeftMouseButton(e)) {
							if (tiles[i2][j2].coverstatus != 1) {
								if (!timer.isRunning())
									timer.start();

								CardLayout cl = (CardLayout) (tiles[i2][j2].panel.getLayout());
								cl.next(tiles[i2][j2].panel);
								tiles[i2][j2].coverstatus = -1;

								if (tiles[i2][j2].valuestatus == -1)
									game_lost(i2, j2);
								else {
									new_game.setIcon(newgame);
									closedsafetiles--;
									if (tiles[i2][j2].valuestatus == 0)
										open_tile(i2, j2);
									if (closedsafetiles <= 0)
										game_won();
								}
							}
						}
					}
				});
				tiles[i][j].panel.add(tiles[i][j].cover);

				tiles[i][j].value = new JLabel();
				tiles[i][j].value.setPreferredSize(new Dimension(32, 32));
				tiles[i][j].value.setBorder(new LineBorder(new Color(64, 64, 64)));
				tiles[i][j].value.setFont(new Font("Consolas", Font.BOLD, 26));
				tiles[i][j].value.setText(null);
				tiles[i][j].value.setHorizontalAlignment(SwingConstants.CENTER);
				tiles[i][j].panel.add(tiles[i][j].value);

				field.add(tiles[i][j].panel);

				l += 32;
			}
			l = 0;
			t += 32;
		}
	}

	// Populates the game field with mines
	private void create_new_mines() {
		Random rnd = new Random();
		int m = mines, x, y;

		while (m > 0) {
			x = rnd.nextInt(16) + 1;
			y = rnd.nextInt(16) + 1;
			if (tiles[x][y].valuestatus == 0) {
				tiles[x][y].valuestatus = -1;
				m--;
			}
		}

		for (int i = 1; i < 17; i++) {
			for (int j = 1; j < 17; j++) {
				if (tiles[i][j].valuestatus != -1) {
					tiles[i][j].valuestatus = 0;
					if (tiles[i - 1][j - 1].valuestatus == -1)
						tiles[i][j].valuestatus++;
					if (tiles[i - 1][j].valuestatus == -1)
						tiles[i][j].valuestatus++;
					if (tiles[i - 1][j + 1].valuestatus == -1)
						tiles[i][j].valuestatus++;
					if (tiles[i][j - 1].valuestatus == -1)
						tiles[i][j].valuestatus++;
					if (tiles[i][j + 1].valuestatus == -1)
						tiles[i][j].valuestatus++;
					if (tiles[i + 1][j - 1].valuestatus == -1)
						tiles[i][j].valuestatus++;
					if (tiles[i + 1][j].valuestatus == -1)
						tiles[i][j].valuestatus++;
					if (tiles[i + 1][j + 1].valuestatus == -1)
						tiles[i][j].valuestatus++;

					if (tiles[i][j].valuestatus > 0) {
						tiles[i][j].value.setText(Integer.toString(tiles[i][j].valuestatus));

						switch (tiles[i][j].valuestatus) {
						case 1:
							tiles[i][j].value.setForeground(new Color(0, 0, 255)); // Blue
							break;
						case 2:
							tiles[i][j].value.setForeground(new Color(0, 128, 0)); // Green
							break;
						case 3:
							tiles[i][j].value.setForeground(new Color(255, 0, 0)); // Red
							break;
						case 4:
							tiles[i][j].value.setForeground(new Color(128, 0, 128)); // Purple
							break;
						case 5:
							tiles[i][j].value.setForeground(new Color(128, 0, 0)); // Brown
							break;
						case 6:
							tiles[i][j].value.setForeground(new Color(0, 255, 255)); // Aqua
							break;
						case 7:
							tiles[i][j].value.setForeground(new Color(0, 0, 0)); // Black
							break;
						case 8:
							tiles[i][j].value.setForeground(new Color(128, 128, 128)); // Gray
							break;
						}
					}
				}
			}
		}
	}

	// Executes if a game is lost
	private void game_lost(int x, int y) {
		new_game.setIcon(gamelost);
		tiles[x][y].value.setIcon(redmine_broken);
		timer.stop();

		for (int i = 1; i < 17; i++) {
			for (int j = 1; j < 17; j++) {
				if (tiles[i][j].coverstatus != -1) {
					if (tiles[i][j].valuestatus == -1) {
						if (tiles[i][j].coverstatus == 1)
							tiles[i][j].cover.setIcon(tile_mine_flag);
						else
							tiles[i][j].cover.setIcon(tile_mine);
					} else {
						if (tiles[i][j].coverstatus == 1) {
							tiles[i][j].cover.setIcon(tile_mine_nomine);
						}
					}
				}
			}
		}

		if (JOptionPane.showConfirmDialog(null, "Sorry, you lost this game. Do you want to start a new one?",
				"Game Lost", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
			try {
				Runtime.getRuntime().exec("java -jar Minesweeper.jar");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}

	// Executes if a game is won
	private void game_won() {
		new_game.setIcon(gamewon);
		timer.stop();

		mines = 0;
		mines_left_value.setText(Integer.toString(mines));

		for (int i = 1; i < 17; i++) {
			for (int j = 1; j < 17; j++) {
				if (tiles[i][j].coverstatus != -1 && tiles[i][j].coverstatus != 1)
					tiles[i][j].cover.setIcon(tile_mine);
			}
		}
		if (JOptionPane.showConfirmDialog(null, "Congratulations, you won the game! Do you want to start a new one?",
				"Game Won", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
			try {
				Runtime.getRuntime().exec("java -jar Minesweeper.jar");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setTitle("Minesweeper - Aleks Angelov");
		frame.setBounds(100, 100, 558, 688);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(minesweeper.getImage());
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);

		mines_left = new JLabel("Mines left:");
		mines_left.setBounds(11, 11, 96, 48);
		mines_left.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 18));
		mines_left.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(mines_left);

		new_game = new JButton("");
		new_game.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (JOptionPane.showConfirmDialog(null, "Do you really want to start a new game?", "New Game",
							JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
						try {
							Runtime.getRuntime().exec("java -jar Minesweeper.jar");
						} catch (IOException ex) {
							ex.printStackTrace();
						}
						System.exit(0);
					}
				}
			}
		});
		new_game.setBounds(227, 11, 96, 96);
		new_game.setIcon(newgame);
		frame.getContentPane().add(new_game);

		time_past = new JLabel("Time past:");
		time_past.setBounds(445, 11, 96, 48);
		time_past.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 18));
		time_past.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(time_past);

		mines_left_value = new JLabel("40");
		mines_left_value.setBounds(11, 59, 96, 48);
		mines_left_value.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 20));
		mines_left_value.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(mines_left_value);

		time_past_value = new JLabel("0");
		time_past_value.setBounds(445, 59, 96, 48);
		time_past_value.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 20));
		time_past_value.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(time_past_value);

		field = new JPanel();
		field.setBorder(new LineBorder(new Color(0, 0, 0)));
		field.setBounds(11, 118, 530, 530);
		frame.getContentPane().add(field);
		field.setLayout(new GridLayout(16, 16, 0, 0));

		timer = new Timer(1000, new ActionListener() {
			// Updates the time past value
			@Override
			public void actionPerformed(ActionEvent e) {
				time_past_value.setText(Integer.toString(++time));
			}
		});

		create_new_field();
		choose_difficulty();
	}

	// Opens all empty tiles and the first non-mine tiles, around a clicked
	// empty tile
	private void open_tile(int x, int y) {
		if (tiles[x - 1][y - 1].coverstatus != -1) {
			CardLayout cl = (CardLayout) (tiles[x - 1][y - 1].panel.getLayout());
			cl.next(tiles[x - 1][y - 1].panel);
			tiles[x - 1][y - 1].coverstatus = -1;
			closedsafetiles--;
			if (tiles[x - 1][y - 1].valuestatus == 0)
				open_tile(x - 1, y - 1);
		}

		if (tiles[x - 1][y].coverstatus != -1) {
			CardLayout cl = (CardLayout) (tiles[x - 1][y].panel.getLayout());
			cl.next(tiles[x - 1][y].panel);
			tiles[x - 1][y].coverstatus = -1;
			closedsafetiles--;
			if (tiles[x - 1][y].valuestatus == 0)
				open_tile(x - 1, y);
		}

		if (tiles[x - 1][y + 1].coverstatus != -1) {
			CardLayout cl = (CardLayout) (tiles[x - 1][y + 1].panel.getLayout());
			cl.next(tiles[x - 1][y + 1].panel);
			tiles[x - 1][y + 1].coverstatus = -1;
			closedsafetiles--;
			if (tiles[x - 1][y + 1].valuestatus == 0)
				open_tile(x - 1, y + 1);
		}

		if (tiles[x][y - 1].coverstatus != -1) {
			CardLayout cl = (CardLayout) (tiles[x][y - 1].panel.getLayout());
			cl.next(tiles[x][y - 1].panel);
			tiles[x][y - 1].coverstatus = -1;
			closedsafetiles--;
			if (tiles[x][y - 1].valuestatus == 0)
				open_tile(x, y - 1);
		}

		if (tiles[x][y + 1].coverstatus != -1) {
			CardLayout cl = (CardLayout) (tiles[x][y + 1].panel.getLayout());
			cl.next(tiles[x][y + 1].panel);
			tiles[x][y + 1].coverstatus = -1;
			closedsafetiles--;
			if (tiles[x][y + 1].valuestatus == 0)
				open_tile(x, y + 1);
		}

		if (tiles[x + 1][y - 1].coverstatus != -1) {
			CardLayout cl = (CardLayout) (tiles[x + 1][y - 1].panel.getLayout());
			cl.next(tiles[x + 1][y - 1].panel);
			tiles[x + 1][y - 1].coverstatus = -1;
			closedsafetiles--;
			if (tiles[x + 1][y - 1].valuestatus == 0)
				open_tile(x + 1, y - 1);
		}

		if (tiles[x + 1][y].coverstatus != -1) {
			CardLayout cl = (CardLayout) (tiles[x + 1][y].panel.getLayout());
			cl.next(tiles[x + 1][y].panel);
			tiles[x + 1][y].coverstatus = -1;
			closedsafetiles--;
			if (tiles[x + 1][y].valuestatus == 0)
				open_tile(x + 1, y);
		}

		if (tiles[x + 1][y + 1].coverstatus != -1) {
			CardLayout cl = (CardLayout) (tiles[x + 1][y + 1].panel.getLayout());
			cl.next(tiles[x + 1][y + 1].panel);
			tiles[x + 1][y + 1].coverstatus = -1;
			closedsafetiles--;
			if (tiles[x + 1][y + 1].valuestatus == 0)
				open_tile(x + 1, y + 1);
		}
	}
}
