package fr.hyper.labyrinth;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class Tile {
	public int x, y, cost = -1;
	public boolean upBlocked = true, downBlocked = true, leftBlocked = true, rightBlocked = true;
	private boolean isStart;
	private boolean isEnd;
	private boolean isVisited = false;
	private Tile lastCaseVisited;
	private Line2D[] wallsHitboxes = new Line2D[4];

	public Tile(int x, int y, boolean isStart, boolean isEnd) {
		this.x = x;
		this.y = y;
		this.isStart = isStart;
		this.isEnd = isEnd;
	}

	public void paint(Graphics graphics){
		Graphics2D g = ((Graphics2D)graphics);
		g.setColor(Color.white);
		if(isStart)
			g.setColor(Color.red);
		if(isEnd)
			g.setColor(Color.green);
		g.fillRect(x, y, 20, 20);
		g.setColor(Color.black);
		for(Line2D toDraw : wallsHitboxes) if(toDraw != null)
			g.drawLine((int)toDraw.getX1(), (int)toDraw.getY1(), (int)toDraw.getX2(), (int)toDraw.getY2());
	}
	
	public Line2D[] getWallsHitboxes() {
		if(upBlocked)
			wallsHitboxes[0] = new Line2D.Double(x,y,x+19,y);
		if(downBlocked)
			wallsHitboxes[1] = new Line2D.Double(x,y+19,x+19,y+19);
		if(leftBlocked)
			wallsHitboxes[2] = new Line2D.Double(x,y,x,y+19);
		if(rightBlocked)
			wallsHitboxes[3] = new Line2D.Double(x+19,y,x+19,y+19);
		return wallsHitboxes;
	}

	public boolean isStart(){
		return isStart;
	}

	public boolean isEnd(){
		return isEnd;
	}

	public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
		if(!isVisited)
			setLastCaseVisited(null);
	}
	
	public void setLastCaseVisited(Tile lastCaseVisited){
		this.lastCaseVisited = lastCaseVisited;
		setVisited(true);
	}
	
	public Tile getLastTileVisited(){
		return lastCaseVisited;
	}
}
