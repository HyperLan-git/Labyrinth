package fr.hyper.maze;
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
	public static final double MOVESPEED = 6;

	private List<Tile> IAPath = new ArrayList<>();

	private Ellipse2D hitbox = new Ellipse2D.Double(1, 1, 10, 10);

	private double speedX = 0, speedY = 0;

	private Tile[][] grid;

	private boolean isIA = false, won = false;

	public Player(Tile[][] cases) {
		this.grid = cases;
	}

	private void calculateIAPath() {
		synchronized(IAPath) {
			IAPath.clear();
			Tile path = grid[Jeu.WIDTH-1][Jeu.HEIGHT-1];
			IAPath.add(path);

			while(path.getLastTileVisited() != null) {
				path = path.getLastTileVisited();
				IAPath.add(path);
			}

			try {
				if(!IAPath.contains(getCurrentTile()))
					runPathfinder(getCurrentTile());
			} catch(Exception e){
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	/**
	 * @param start
	 */
	private void runPathfinder(Tile start) {
		ArrayList<Tile> openSet = new ArrayList<>(), closedSet = new ArrayList<>();
		Tile end = grid[Jeu.WIDTH-1][Jeu.HEIGHT-1],
				curr = start,
				path[];
		openSet.add(end);
		end.cost = 0;
		while(!openSet.isEmpty()) {
			for(int i = openSet.size()-1; i >= 0; i--) {
				Tile c = openSet.get(i);
				openSet.remove(i);
				if(c == start) {
					Tile currentTile = end;
					int dist = c.cost;
					Tile[] result = new Tile[dist+1];
					while(dist >= 0) {
						result[dist--] = currentTile;
						currentTile = currentTile.getLastTileVisited();
					}
					openSet.clear();
					break;
				}
				for(Tile neighbor : getTilesAroundAccessible(c))
					if((neighbor.cost < 0 || neighbor.cost > c.cost+1) && (!closedSet.contains(neighbor) || !openSet.contains(neighbor))) {
						neighbor.cost = c.cost+1;
						openSet.add(neighbor);
						neighbor.setLastTileVisited(c);
					}
				closedSet.add(c);
			}
		}
		path = new Tile[start.cost+1];
		while(curr != end) {
			int cost = start.cost;
			System.out.println(curr.x + ", " + curr.y);
			System.out.println("cost : " + curr.cost);
			path[curr.cost] = curr;
			for(Tile c : getTilesAroundAccessible(curr)) {
				if(c.cost >= 0 && c.cost < cost) {
					cost = c.cost;
					curr = c;
				}
			}
		}
		path[0] = end;
		synchronized(IAPath) {
			IAPath = Arrays.asList(path);
		}
	}

	public ArrayList<Tile> getTilesAroundAccessible(Tile c){
		ArrayList<Tile> result = new ArrayList<>();
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
				speedY = MOVESPEED;
			else if(e.getID() == KeyEvent.KEY_RELEASED)
				speedY = 0;
			break;
		case KeyEvent.VK_UP:
			if(e.getID() == KeyEvent.KEY_PRESSED)
				speedY = -MOVESPEED;
			else if(e.getID() == KeyEvent.KEY_RELEASED)
				speedY = 0;
			break;
		case KeyEvent.VK_RIGHT:
			if(e.getID() == KeyEvent.KEY_PRESSED)
				speedX = MOVESPEED;
			else if(e.getID() == KeyEvent.KEY_RELEASED)
				speedX = 0;
			break;
		case KeyEvent.VK_LEFT:
			if(e.getID() == KeyEvent.KEY_PRESSED)
				speedX = -MOVESPEED;
			else if(e.getID() == KeyEvent.KEY_RELEASED)
				speedX = 0;
			break;
		}
	}

	private void checkCollisions() {
		boolean hasCollided = false;
		for(Tile[] ligne : grid) for(Tile c : ligne) {
			for(int i = 0; i < 4; i++) {
				Line2D s = c.getWallsHitboxes()[i];
				if(s != null && s.ptSegDistSq(this.getX(), this.getY()) < 25) {
					boolean horizontal = s.getY1() == s.getY2();
					hasCollided = true;
					if(horizontal) {
						boolean isUp = this.getY() < s.getY1();
						if(isUp)
							move(0, -MOVESPEED/4);
						else
							move(0, MOVESPEED/4);
					} else {
						boolean isLeft = this.getX() < s.getX1();
						if(isLeft)
							move(-MOVESPEED/4, 0);
						else
							move(MOVESPEED/4, 0);
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
		synchronized(IAPath) {for(Tile c : IAPath)
			g.drawRect(c.x+1, c.y+1, 17, 17);
		}
		g.setColor(Color.BLACK);
		((Graphics2D)g).fill(hitbox);
	}

	private Tile getCurrentTile() {
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
		//We got quartersteps this is mario 64 tech right there
		for(int i = 0; i < 4; i++) {
			if(isIA) {
				this.speedX = 0;
				this.speedY = 0;
				Tile c = getCurrentTile();
				synchronized(IAPath) {
					if(!IAPath.contains(c))
						runPathfinder(c);
					int index = IAPath.indexOf(c)-1;
					if(index >= 0) {
						Tile last = IAPath.get(index);

						if(this.getX() >= last.x+10)
							speedX = -MOVESPEED;
						else
							speedX = MOVESPEED;
						if(this.getY() > last.y+10)
							speedY = -MOVESPEED;
						else
							speedY = MOVESPEED;
					}
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
		move(speedX/4.0, speedY/4.0);
		checkCollisions();
	}
}
