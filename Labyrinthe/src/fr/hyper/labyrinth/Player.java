package fr.hyper.labyrinth;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

public class Player {
	public static final double movespeed = 4;

	private List<Case> IAPath = new ArrayList<>();

	private Ellipse2D hitbox = new Ellipse2D.Double(1, 1, 10, 10);

	private double speedX = 0, speedY = 0;

	private Case[][] grid;

	private boolean isIA = false, won = false;

	public Player(Case[][] cases) {
		this.grid = cases;
	}

	private void calculateIAPath() {
		IAPath.clear();
		Case path = grid[Jeu.WIDTH-1][Jeu.HEIGHT-1];
		IAPath.add(path);

		while(path.getLastCaseVisited() != null) {
			path = path.getLastCaseVisited();
			IAPath.add(path);
		}

		try {
			if(!IAPath.contains(getCurrentCase()))
				runPathFinder(getCurrentCase());
		} catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * @param start
	 */
	private void runPathFinder(Case start) {
		ArrayList<Case> openSet = new ArrayList<>(), closedSet = new ArrayList<>();
		Case end = grid[Jeu.WIDTH-1][Jeu.HEIGHT-1],
				curr = start,
				path[];
		openSet.add(end);
		end.cost = 0;
		while(!openSet.isEmpty()) {
			for(int i = openSet.size()-1; i >= 0; i--) {
				Case c = openSet.get(i);
				openSet.remove(i);
				if(c == start) {
					Case currentCase = end;
					int dist = c.cost;
					Case[] result = new Case[dist+1];
					for(; dist >= 0 ; dist--) {
						result[dist] = currentCase;
						currentCase = currentCase.getLastCaseVisited();
					}
					return;
				}
				for(Case neighbor : getCasesAroundAccessible(c))
					if((neighbor.cost < 0 || neighbor.cost > c.cost+1) && (!closedSet.contains(neighbor) || !openSet.contains(neighbor))) {
						neighbor.cost = c.cost+1;
						openSet.add(neighbor);
						neighbor.setLastCaseVisited(c);
					}
				closedSet.add(c);
			}
		}
		path = new Case[start.cost+1];
		while(curr != end) {
			int cost = start.cost;
			System.out.println("cost : " + curr.cost);
			path[curr.cost] = curr;
			for(Case c : getCasesAroundAccessible(curr)) {
				if(c.cost >= 0 && c.cost < cost) {
					cost = c.cost;
					curr = c;
				}
			}
		}
		path[0] = end;
		IAPath = Arrays.asList(path);
	}

	public ArrayList<Case> getCasesAroundAccessible(Case c){
		ArrayList<Case> result = new ArrayList<>();
		if(c == null)
			return null;
		if(c.x/20 < Jeu.WIDTH-1 && !c.rightBlocked)
			result.add(grid[c.x/20+1][c.y/20]);
		if(c.x/20 > 0 && !c.leftBlocked)
			result.add(grid[c.x/20-1][c.y/20]);
		if(c.y/20 < Jeu.HEIGHT-1 && !c.downBlocked)
			result.add(grid[c.x/20][c.y/20+1]);
		if(c.y/20 > 0 && !c.upBlocked)
			result.add(grid[c.x/20][c.y/20-1]);
		return result;
	}

	public void onKeyEvent(KeyEvent e){
		if(!isIA) switch(e.getKeyCode()) {
		case KeyEvent.VK_DOWN:
			if(e.getID() == KeyEvent.KEY_PRESSED)
				speedY = movespeed;
			else if(e.getID() == KeyEvent.KEY_RELEASED)
				speedY = 0;
			break;
		case KeyEvent.VK_UP:
			if(e.getID() == KeyEvent.KEY_PRESSED)
				speedY = -movespeed;
			else if(e.getID() == KeyEvent.KEY_RELEASED)
				speedY = 0;
			break;
		case KeyEvent.VK_RIGHT:
			if(e.getID() == KeyEvent.KEY_PRESSED)
				speedX = movespeed;
			else if(e.getID() == KeyEvent.KEY_RELEASED)
				speedX = 0;
			break;
		case KeyEvent.VK_LEFT:
			if(e.getID() == KeyEvent.KEY_PRESSED)
				speedX = -movespeed;
			else if(e.getID() == KeyEvent.KEY_RELEASED)
				speedX = 0;
			break;
		}
	}

	private void checkCollisions() {
		boolean hasCollided = false;
		for(Case[] ligne : grid) for(Case c : ligne) {
			for(int i = 0; i < 4; i++) {
				Line2D s = c.getWallsHitboxes()[i];
				if(s != null && s.ptSegDistSq(this.getX(), this.getY()) < 25) {
					boolean horizontal = s.getY1() == s.getY2();
					hasCollided = true;
					if(horizontal) {
						boolean isUp = this.getY() < s.getY1();
						if(isUp)
							move(0, -movespeed/4);
						else
							move(0, movespeed/4);
					} else {
						boolean isLeft = this.getX() < s.getX1();
						if(isLeft)
							move(-movespeed/4, 0);
						else
							move(movespeed/4, 0);
					}
				}
			}
		}
		if(hasCollided)
			checkCollisions();
	}

	private void move(double x, double y) {
		this.hitbox = new Ellipse2D.Double(hitbox.getMinX()+x, hitbox.getMinY()+y, 10, 10);
	}

	public void paint(Graphics g) {
		g.setColor(Color.pink);
		for(Case c : IAPath) {
			g.drawRect(c.x+1, c.y+1, 17, 17);
		}
		g.setColor(Color.BLACK);
		((Graphics2D)g).fill(hitbox);
	}

	private Case getCurrentCase() {
		return grid[(int)(this.getX()/20.0)][(int)(this.getY()/20.0)];
	}

	public void setIA(boolean IA) {
		isIA = IA;
		if(!isIA) {
			this.speedX = 0;
			this.speedY = 0;
		} else
			calculateIAPath();
	}

	public double getX() {
		return this.hitbox.getCenterX();
	}

	public double getY() {
		return this.hitbox.getCenterY();
	}

	public boolean isIA() {
		return isIA;
	}

	public void update() {
		for(int i = 0; i < 2; i++) {
			if(isIA) {
				this.speedX = 0;
				this.speedY = 0;
				Case c = getCurrentCase();
				if(!IAPath.contains(c))
					runPathFinder(c);
				int index = IAPath.indexOf(c)-1;
				if(index >= 0) {
					Case last = IAPath.get(index);

					if(this.getX() >= last.x+10)
						speedX = -movespeed;
					else
						speedX = movespeed;
					if(this.getY() > last.y+10)
						speedY = -movespeed;
					else
						speedY = movespeed;
				}
			}
			moveStep();
		}

		if(won)
			return;

		if(this.hitbox.getMaxX() >= grid[Jeu.WIDTH-1][Jeu.HEIGHT-1].x &&
				this.hitbox.getMaxY() >= grid[Jeu.WIDTH-1][Jeu.HEIGHT-1].y) {
			won = true;
			JOptionPane.showMessageDialog(null, "Vous avez gagné !", "Félicitations !", JOptionPane.INFORMATION_MESSAGE);
			setIA(false);
			speedX = 0;
			speedY = 0;
			return;
		}
	}

	private void moveStep() {
		move(speedX/2.0, speedY/2.0);
		checkCollisions();
	}
}
