import javax.swing.*;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
/*import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.FlowLayout;
*/
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.KeyEvent;
//import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Random;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.SwingUtilities;
import java.awt.geom.AffineTransform;
import java.lang.Math;

public class TronFrame extends JFrame implements KeyListener
{
	public static void main(String[] args) throws Exception {
		TronFrame gameFrame = new TronFrame();
		gameFrame.playSet();
	}

	public int[][] cellstates;
	private TronCycle blueCycle;
	private TronCycle orangeCycle;
	// 0 = empty
	// 1 = blue
	// 2 = orange
	// 3 = death
	// 4 = bluerecent
	// 5 = orangerecent
	public static final int DEATH_COLOR = 3;
	public static final Color blueColor = new Color(0,0,255);
	public static final Color orangeColor = new Color(255,150,0);
	public static final Color blueRecentColor = new Color(50,50,255);
	public static final Color orangeRecentColor = new Color(255,180,50);
	public static final Color deathColor = new Color(200,0,0);
	private TronPanel tp;

	private int[] scores;
	public TronFrame() {
		super("Tron Test");
		startStuff();
		addKeyListener(this);
		this.setResizable(false);
		this.setSize(400,400); // somewhat irrelevant
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = this.getContentPane();
		tp = new TronPanel();
		contentPane.add(tp);
		this.pack();
		scores = new int[5];
		for(int i = 0; i < 5; i ++) {
			scores[i] = -1;
		}
	}

	public void playSet() throws Exception {
		int winner;
		for(int j = 0; j < 5; j ++) {
			winner = 3;
			while(winner == 3) {
				startStuff();
				startAnimation();
				winner = playGame();
				Thread.sleep(1000);
			}
			scores[j] = winner;
			this.repaint();
			Thread.sleep(250);
		}
	}

	public int playGame() throws Exception {
		return playGame(75);
	}

	public int playGame(int tickspeed) throws Exception {
		while(blueCycle.isAlive && orangeCycle.isAlive) {
			Thread.sleep(tickspeed);
			this.act();
		}
		if(blueCycle.isAlive)
			return 1;
		else if(orangeCycle.isAlive)
			return 2;
		else
			return 3;
	}

	public void startStuff() {
		blueCycle = new TronCycle(10,20,2,1,4);
		orangeCycle = new TronCycle(30,20,4,2,5);
		cellstates = new int[41][41];
		/*for(int i = 0; i < 41; i ++) {
			for(int j = 0; j < 41; j ++) {
				cellstates[i][j] = 0;
			}
		}*/
	}

	public void startAnimation() throws Exception {
		startAnimation(75);
	}

	public void startAnimation(int tickspeed) throws Exception {
		for(int i = 0; i < 3; i ++) {
			cellstates[10][20] = 4;
			cellstates[30][20] = 5;
			this.repaint();
			orangeCycle.hasChangedDir = false;
			blueCycle.hasChangedDir = false;
			Thread.sleep(5*tickspeed);
			cellstates[10][20] = 0;
			cellstates[30][20] = 0;
			this.repaint();
			Thread.sleep(2*tickspeed);
			orangeCycle.hasChangedDir = false;
			blueCycle.hasChangedDir = false;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();

		switch(code) {
			case KeyEvent.VK_UP:
				if(orangeCycle.dir != 3 && !orangeCycle.hasChangedDir) {
					orangeCycle.dir = 1;
					orangeCycle.hasChangedDir = true;
				}
				break;
			case KeyEvent.VK_RIGHT:
				if(orangeCycle.dir != 4 && !orangeCycle.hasChangedDir) {
					orangeCycle.dir = 2;
					orangeCycle.hasChangedDir = true;
				}
				break;
			case KeyEvent.VK_DOWN:
				if(orangeCycle.dir != 1 && !orangeCycle.hasChangedDir) {
					orangeCycle.dir = 3;
					orangeCycle.hasChangedDir = true;
				}
				break;
			case KeyEvent.VK_LEFT:
				if(orangeCycle.dir != 2 && !orangeCycle.hasChangedDir) {
					orangeCycle.dir = 4;
					orangeCycle.hasChangedDir = true;
				}
				break;
			case KeyEvent.VK_W:
				if(blueCycle.dir != 3 && !blueCycle.hasChangedDir) {
					blueCycle.dir = 1;
					blueCycle.hasChangedDir = true;
				}
				break;
			case KeyEvent.VK_D:
				if(blueCycle.dir != 4 && !blueCycle.hasChangedDir) {
					blueCycle.dir = 2;
					blueCycle.hasChangedDir = true;
				}
				break;
			case KeyEvent.VK_S:
				if(blueCycle.dir != 1 && !blueCycle.hasChangedDir) {
					blueCycle.dir = 3;
					blueCycle.hasChangedDir = true;
				}
				break;
			case KeyEvent.VK_A:
				if(blueCycle.dir != 2 && !blueCycle.hasChangedDir) {
					blueCycle.dir = 4;
					blueCycle.hasChangedDir = true;
				}
				break;

			default:
				break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	public void act() {
		blueCycle.move(cellstates);
		orangeCycle.move(cellstates);
		this.repaint();
		if(orangeCycle.X == blueCycle.X && orangeCycle.Y == blueCycle.Y) {
			cellstates[orangeCycle.lX][orangeCycle.lY] = orangeCycle.color;
			cellstates[blueCycle.lX][blueCycle.lY] = blueCycle.color;
			cellstates[blueCycle.X][blueCycle.Y] = TronFrame.DEATH_COLOR;
			blueCycle.isAlive = false;
			orangeCycle.isAlive = false;
		}
	}

	private class TronCycle {
		public int X;
		public int Y;
		private int lX;
		private int lY;
		private int initDir;
		public int dir;
		public int color;
		public int scolor;
		public boolean hasChangedDir;
		public boolean isAlive;
		/*
		 *       1
		 *     4-+-2
		 *       3
		 */
		public TronCycle(int initX, int initY, int iDir, int rColor, int tColor) {
			X = initX;
			Y = initY;
			dir = 5;
			initDir = iDir;
			color = rColor;
			scolor = tColor;
			hasChangedDir = false;
			isAlive = true;
		}

		private void moveForward() {
			switch(dir) {
				case 1: Y--; break;
				case 2: X++; break;
				case 3: Y++; break;
				case 4: X--; break;
				default: break;
			}
		}

		public void move(int[][] cells) {
			if(dir == 5) {
				dir = initDir;
			}
			cells[X][Y] = color;
			lX = X;
			lY = Y;
			moveForward();
			if(X >= 0 && Y >= 0 && X < cells.length && Y < cells[X].length) {
				if(cells[X][Y] == 0) {
					cells[X][Y] = scolor;
				}
				else {	// hit trail
					cells[lX][lY] = TronFrame.DEATH_COLOR;
					isAlive = false;
				}
			}
			else { // out of bounds
				cells[lX][lY] = TronFrame.DEATH_COLOR;
				isAlive = false;
			}
			hasChangedDir = false;
		} 
	}


	private class TronPanel extends JPanel {
		public TronPanel() {
			super();
		}

		// 41 x 41
		// 30 px barrier on top for score meter
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(new Color(70,70,70));
			g.fillRect(0,0,472,502);
			g.setColor(new Color(0,0,0));
			g.fillRect(170,16,132,28);
			g.fillRect(30,60,412,412);
			g.setColor(new Color(20,20,70));
			for(int i = 0; i < 42; i ++) {
				g.drawLine(30,60+10*i,441,60+10*i);
				g.drawLine(30,60+10*i+1,441,60+10*i+1);
				g.drawLine(30+10*i,60,30+10*i,471);
				g.drawLine(30+10*i+1,60,30+10*i+1,471);
			}
			g.drawLine(170,14,302,14);
			g.drawLine(170,15,302,15);
			g.drawLine(170,16,302,16);
			g.drawLine(170,17,302,17);
			g.drawLine(170,42,302,42);
			g.drawLine(170,43,302,43);
			g.drawLine(170,44,302,44);
			g.drawLine(170,45,302,45);
			g.drawLine(169,14,169,45);
			g.drawLine(168,14,168,45);
			g.drawLine(302,14,302,45);
			g.drawLine(303,14,303,45);
			for(int i = 0; i <= 5; i ++) {
				g.drawLine(170+26*i,14,170+26*i,45);
				g.drawLine(170+26*i+1,14,170+26*i+1,45);
			}
			for(int i = 0; i < 5; i ++) {
				if(scores[i] == 1) {
					g.setColor(blueColor);
					g.fillRect(172+26*i,18,24,24);
				}
				else if(scores[i] == 2) {
					g.setColor(orangeColor);
					g.fillRect(172+26*i,18,24,24);
				}
			}
			int tmp;
			for(int i = 0; i < 41; i ++) {
				for(int j = 0; j < 41; j ++) {
					tmp = cellstates[i][j];
					if(tmp == 1) {
						g.setColor(blueColor);
						g.fillRect(30+10*i+2,60+10*j+2,8,8);
					}
					else if(tmp == 2) {
						g.setColor(orangeColor);
						g.fillRect(30+10*i+2,60+10*j+2,8,8);
					}
					else if(tmp == 3) {
						g.setColor(deathColor);
						g.fillRect(30+10*i+2,60+10*j+2,8,8);
					}
					else if(tmp == 4) {
						g.setColor(blueRecentColor);
						g.fillRect(30+10*i+2,60+10*j+2,8,8);
					}
					else if(tmp == 5) {
						g.setColor(orangeRecentColor);
						g.fillRect(30+10*i+2,60+10*j+2,8,8);
					}
				}
			}
		}

		public Dimension getPreferredSize() {
			return new Dimension(472,502);
		}
	}
}