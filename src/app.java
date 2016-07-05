package all;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.dnd.DropTarget;

import javax.swing.JFrame;
import javax.swing.JLabel;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.Font;

@SuppressWarnings("serial")
public class app extends javax.swing.JPanel{

	private DragListener d;
	public JFrame frame;
	private int APP_W,APP_H;
	private JLabel lblInfolabel;
	private JButton btnXReflex;
	private JButton btnYReflex;
	private JLabel lblModes;
	private JRadioButton opCW;
	private JRadioButton opCCW;
	private JButton btn90;
	private JButton btn180;
	private JButton btn270;
	private JLabel lblRotations;
	private JPanel panel;
	private JLabel ImageLabel;
	public JLabel PathLabel;
	private ButtonGroup bg;
	public boolean mode;
	public BMPFile bmp;
	private JButton btnNegative;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					app window = new app();
					Image image = ImageIO.read(new File("HalfLife.png"));
					window.frame.setIconImage(image);
					window.frame.setTitle("BMP Editor");
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public app(){
		initialize();
		connectToDragDrop();
	}
	
	private void connectToDragDrop(){
		d = new DragListener(this,ImageLabel,PathLabel);
		new DropTarget(ImageLabel,d);
	}

	private void initialize(){
		Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
		mode = true;
		APP_H	= 700;
		APP_W	= 800;
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.DARK_GRAY);
		frame.setBounds(sSize.width/2 - APP_W/2, sSize.height/2 - APP_H/2, APP_W, APP_H);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		lblInfolabel = new JLabel("Operations");
		lblInfolabel.setForeground(Color.WHITE);
		lblInfolabel.setBounds(141, 11, 77, 23);
		frame.getContentPane().add(lblInfolabel);
		
		btnNegative = new JButton("Negative");
		btnNegative.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				d.Negative();
			}
		});
		btnNegative.setBounds(23, 45, 92, 23);
		frame.getContentPane().add(btnNegative);
		
		btnXReflex = new JButton("X Reflec");
		btnXReflex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				d.ReflecX();
			}
		});
		btnXReflex.setBounds(126, 45, 92, 23);
		frame.getContentPane().add(btnXReflex);
		
		btnYReflex = new JButton("Y Reflec");
		btnYReflex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				d.ReflecY();
			}
		});
		btnYReflex.setBounds(228, 45, 92, 23);
		frame.getContentPane().add(btnYReflex);
		
		lblModes = new JLabel("Modes");
		lblModes.setForeground(Color.WHITE);
		lblModes.setBounds(406, 15, 46, 14);
		frame.getContentPane().add(lblModes);
		
		opCW = new JRadioButton("CW");
		opCW.setForeground(Color.WHITE);
		opCW.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mode = true;
			}
		});
		opCW.setBackground(Color.DARK_GRAY);
		opCW.setSelected(true);
		opCW.setBounds(372, 45, 55, 23);
		frame.getContentPane().add(opCW);
		
		opCCW = new JRadioButton("CCW");
		opCCW.setForeground(Color.WHITE);
		opCCW.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mode = false;
			}
		});
		opCCW.setBackground(Color.DARK_GRAY);
		opCCW.setBounds(429, 45, 55, 23);
		frame.getContentPane().add(opCCW);
		
		btn90 = new JButton("90\u00BA");
		btn90.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				d.rotate(mode, 90);
			}
		});
		btn90.setBounds(544, 45, 66, 23);
		frame.getContentPane().add(btn90);
		
		btn180 = new JButton("180\u00BA");
		btn180.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				d.rotate(mode, 180);
			}
		});
		btn180.setBounds(619, 45, 66, 23);
		frame.getContentPane().add(btn180);
		
		btn270 = new JButton("270\u00BA");
		btn270.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				d.rotate(mode, 270);
			}
		});
		btn270.setBounds(693, 45, 66, 23);
		frame.getContentPane().add(btn270);
		
		lblRotations = new JLabel("Rotations");
		lblRotations.setForeground(Color.WHITE);
		lblRotations.setBounds(625, 15, 60, 14);
		frame.getContentPane().add(lblRotations);
		
		panel = new JPanel();
		panel.setBounds(141, 124, 512, 512);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		ImageLabel = new JLabel("  Drop your bmp file here!");
		ImageLabel.setFont(new Font("Century Gothic", Font.PLAIN, 40));
		ImageLabel.setBounds(0, 0, 512, 512);
		panel.add(ImageLabel);
		
		PathLabel = new JLabel("Path of Image");
		PathLabel.setForeground(Color.WHITE);
		PathLabel.setBounds(23, 90, 736, 23);
		frame.getContentPane().add(PathLabel);
		
		bg = new ButtonGroup();
		bg.add(opCW);
		bg.add(opCCW);
	}
}
