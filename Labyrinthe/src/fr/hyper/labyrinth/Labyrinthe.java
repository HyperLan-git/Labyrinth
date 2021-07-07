package fr.hyper.labyrinth;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Labyrinthe extends JFrame implements KeyListener {
	private static final long serialVersionUID = 1695010021810078456L;

	public static final double FPS = 60;

	private Jeu jeu;

	public static void main(String[] args) {
		new Labyrinthe().setVisible(true);
	}

	public Labyrinthe(){
		setBackground(Color.WHITE);
		setTitle("Le labyrinthe ultime");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jeu = new Jeu();
		jeu.setBounds(0, 0, Jeu.WIDTH*20, Jeu.HEIGHT*20);
		jeu.setLocation(0, 0);
		jeu.setForeground(Color.BLACK);
		jeu.setLayout(null);
		setContentPane(jeu);

		JButton btnNewButton = new JButton("Mode IA");
		btnNewButton.setSize(180, 60);
		btnNewButton.setLocation(10, Jeu.HEIGHT*20+10);
		jeu.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Mode CPT");
		btnNewButton_1.setEnabled(false);
		btnNewButton_1.setSize(180, 60);
		btnNewButton_1.setLocation(200, Jeu.HEIGHT*20+10);
		jeu.add(btnNewButton_1);
		btnNewButton_1.addKeyListener(this);
		btnNewButton.addKeyListener(this);
		this.addKeyListener(this);
		btnNewButton_1.setVisible(true);
		setResizable(false);
		setSize(Jeu.WIDTH*20+6, Jeu.HEIGHT*20+110);
		setVisible(true);
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnNewButton_1.setEnabled(true);
				btnNewButton_1.grabFocus();
				btnNewButton.setEnabled(false);
				jeu.getPlayer().setIA(true);
			}
		});
		btnNewButton_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnNewButton_1.setEnabled(false);
				btnNewButton.grabFocus();
				btnNewButton.setEnabled(true);
				jeu.getPlayer().setIA(false);
			}
		});
		btnNewButton.addKeyListener(this);
		btnNewButton_1.addKeyListener(this);
		long lastFrame = System.currentTimeMillis();
		while(true) {
			this.repaint();
			if(lastFrame + 1000/FPS < System.currentTimeMillis()) {
				lastFrame = System.currentTimeMillis();
				jeu.update();
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		jeu.registerKeyEvent(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		jeu.registerKeyEvent(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {	}
}
