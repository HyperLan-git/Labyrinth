package fr.hyper.labyrinth;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class Jeu extends JPanel {
	private static final long serialVersionUID = -7756976894843229936L;
	public static final int WIDTH = 90, HEIGHT = 47; 
	private Tile[][] cases = new Tile[WIDTH][HEIGHT];
	private Player p;
	private List<Point2D> playerPos = new ArrayList<>();
	public Jeu() {
		reset();
	}
	
	public void reset() {
		for(int x = 0; x < WIDTH; x++)
			for(int y = 0; y < HEIGHT; y++)
				cases[x][y] = new Tile(x*20, y*20, (y==0 && x==0), (y == HEIGHT-1 && x == WIDTH-1));
		Tile currentTile = cases[0][0];
		while(currentTile != null) {
			List<Tile> possibleTiles = new ArrayList<Tile>();
			for(Tile c : getTilesAround(currentTile))
				if(c != null && !c.isVisited())
					possibleTiles.add(c);
			Tile newTile = (possibleTiles.size()>0)?possibleTiles.get((int)(Math.random()*possibleTiles.size())):null;
			currentTile.setVisited(true);
			if(newTile == null)
				currentTile = currentTile.getLastTileVisited();
			else {
				if(currentTile.x > newTile.x) {
					newTile.rightBlocked = false;
					currentTile.leftBlocked = false;
				} else if(currentTile.x < newTile.x) {
					newTile.leftBlocked = false;
					currentTile.rightBlocked = false;
				} else if(currentTile.y > newTile.y) {
					currentTile.upBlocked = false;
					newTile.downBlocked = false;
				} else if(currentTile.y < newTile.y) {
					currentTile.downBlocked = false;
					newTile.upBlocked = false;
				}
				newTile.setLastTileVisited(currentTile);
				currentTile = newTile;
			}
		}
		p = new Player(cases);
	}

	public Tile[] getTilesAround(Tile c) {
		Tile[] result = new Tile[4];
		if(c.x/20<WIDTH-1)
			result[0] = cases[c.x/20+1][c.y/20];
		if(c.x/20>0)
			result[1] = cases[c.x/20-1][c.y/20];
		if(c.y/20<HEIGHT-1)
			result[2] = cases[c.x/20][c.y/20+1];
		if(c.y/20>0)
			result[3] = cases[c.x/20][c.y/20-1];
		return result;
	}
	
	public void update() {
		p.update();
		playerPos.add(new Point((int)p.getX(), (int)p.getY()));
	}

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);
		for(Tile[] ligne : cases)
			for(Tile c : ligne)
				c.paint(graphics);
		graphics.setColor(Color.LIGHT_GRAY);
		for(int i = 0; i < playerPos.size()-1; i++) {
			Point2D p1 = playerPos.get(i),
					p2 = playerPos.get(i+1);
			graphics.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
		}
		p.paint(graphics);
	}

	public void registerKeyEvent(KeyEvent e) {
		p.onKeyEvent(e);
	}
	
	public Player getPlayer() {
		return p;
	}
}
