import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class DifficultyWindow extends JDialog {

	private JSlider difficulty_scale;

	/**
	 * Create the dialog.
	 */
	public DifficultyWindow() {
		setModal(true);
		setResizable(false);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				System.exit(0);
			}
		});
		setTitle("Minesweeper - Aleks Angelov");
		setBounds(100, 100, 290, 190);
		ImageIcon minesweeper_icon = new ImageIcon("images/minesweeper.png");
		setIconImage(minesweeper_icon.getImage());
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);

		JTextArea difficulty_text = new JTextArea();
		difficulty_text.setBounds(93, 11, 106, 32);
		difficulty_text.setBackground(SystemColor.control);
		difficulty_text.setEditable(false);
		difficulty_text.setFont(new Font("Arial", Font.PLAIN, 12));
		difficulty_text.setText("Number of mines: \r\n   (Default is 40)");
		difficulty_text.setRows(2);
		getContentPane().add(difficulty_text);

		JLabel difficulty_value = new JLabel("40");
		difficulty_value.setBounds(0, 44, 284, 32);
		difficulty_value.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(difficulty_value);

		difficulty_scale = new JSlider();
		difficulty_scale.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				difficulty_value.setText(Integer.toString(difficulty_scale.getValue()));
			}
		});
		difficulty_scale.setValue(40);
		difficulty_scale.setMinimum(10);
		difficulty_scale.setMaximum(232);
		difficulty_scale.setBounds(0, 74, 284, 32);
		getContentPane().add(difficulty_scale);

		JLabel difficulty_limits = new JLabel(
				"  10                                                                              232");
		difficulty_limits.setBounds(0, 100, 284, 32);
		getContentPane().add(difficulty_limits);

		JButton difficulty_start = new JButton("Start!");
		difficulty_start.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dispose();
			}
		});
		difficulty_start.setBounds(89, 117, 106, 32);
		getContentPane().add(difficulty_start);

	}

	public int level() {
		return difficulty_scale.getValue();
	}
}
