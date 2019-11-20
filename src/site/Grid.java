package site;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

import processing.core.PApplet;
import tools.BoolTool;
import tools.Number;
import tools.TransTool;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Vector;
import wblut.processing.WB_Render;

/**
 * �����������ù�ϵ������
 * 
 * @author Naturalpowder
 *
 */
public class Grid {
	/**
	 * �����ȷ���ĸ�����Ŀ
	 */
	private int width;
	/**
	 * ����߶ȷ���ĸ�����Ŀ
	 */
	private int height;
	/**
	 * ����Ԫ�ı߳�
	 */
	private double unit;
	/**
	 * �����ļ���
	 */
	private Collection2D<WB_Point> points;
	/**
	 * ����ϸ���ļ���
	 */
	private Collection2D<Cell> cells;
	/**
	 * �����ԭ������
	 */
	private WB_Point origin;
	/**
	 * ��Ⱦ��
	 */
	private WB_Render render;
	/**
	 * Processing��
	 */
	PApplet app;

	/**
	 * ��ʼ����
	 */
	public Grid() {
	}

	/**
	 * ��ʼ������
	 * 
	 * @param width  x����������Ŀ
	 * @param height y����������Ŀ
	 * @param unit   ��Ԫ�߳�
	 */
	public Grid(int width, int height, double unit) {
		this.width = width;
		this.height = height;
		this.unit = unit;
		points = new Collection2D<>();
		cells = new Collection2D<>();
		points.setOffset(width + 1);
		cells.setOffset(width);
	}

	/**
	 * ��ȡ���������߽�����
	 * 
	 * @return
	 */
	public WB_Polygon getSite() {
		List<WB_Polygon> polys = getAllCellPolygons();
		Geometry result = TransTool.toJTS(polys.get(0));
		for (int i = 1; i < polys.size(); i++) {
			result = result.union(TransTool.toJTS(polys.get(i)));
		}
		return TransTool.toWb_Polygon((Polygon) result);
	}

	/**
	 * �趨��ʼ����
	 * 
	 * @param origin ԭ��
	 * @param app    Processing��
	 */
	public void initialize(WB_Point origin, PApplet app) {
		this.app = app;
		this.origin = origin;
		render = new WB_Render(app);
		setPoints();
		setGrids();
		move(this.origin);
		// rotate(Math.toRadians(-3.63));
	}

	/**
	 * �����ڵ�ԭ��Ϊ�ƶ�����������ĵ�����ƶ��������
	 */
	public void centerMove() {
		WB_Point center = getSite().getCenter();
		move(origin.sub(center));
	}

	/**
	 * �ƶ�����
	 * 
	 * @param vector
	 */
	public void move(WB_Vector vector) {
		for (int i = 0; i < points.size(); i++) {
			WB_Point p = points.get(i);
			points.set(i, p.add(vector));
		}
	}

	/**
	 * ��������ϸ���Ե�����ù�ϵ
	 */
	private void setGrids() {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Cell cell = new Cell();
				cell.add(j, i);
				cell.add(j + 1, i);
				cell.add(j + 1, i + 1);
				cell.add(j, i + 1);
				cells.add(cell);
			}
		}
	}

	/**
	 * ��ȡ�߽������е�����ϸ����Ӧ�Ķ����
	 * 
	 * @return
	 */
	public List<WB_Polygon> getAllCellPolygons() {
		List<WB_Polygon> list = new ArrayList<>();
		List<Cell> cellsInSite = getAllCells();
		for (Cell cell : cellsInSite) {
			list.add(getCellPolygon(cell));
		}
		return list;
	}

	/**
	 * ��ȡ�߽������е�����ϸ��
	 * 
	 * @return
	 */
	public List<Cell> getAllCells() {
		List<Cell> list = new ArrayList<>();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Cell cell = cells.get(j, i);
				if (cell.getStatus() != Cell.OUT) {
					list.add(cell);
				}
			}
		}
		return list;
	}

	/**
	 * ��һ���߽��������вü�����ȡ�ü���ֻ�ڱ߽��ڵ�����
	 * 
	 * @param site
	 */
	public void trim(WB_Polygon site) {
		WB_Polygon site2D = site;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Cell cell = cells.get(j, i);
				WB_Polygon unitCell = new WB_Polygon(points.get(cell));
				BoolTool tool = new BoolTool(site2D, unitCell);
				boolean contain = tool.contains();
				if (!contain)
					cell.setStatus(Cell.OUT);
			}
		}
	}

	/**
	 * ������һ���ǶȽ�����ת
	 * 
	 * @param angle ��ת�Ƕ�
	 */
	public void rotate(double angle) {
		WB_Point center = getSite().getCenter();
		for (int i = 0; i < points.size(); i++) {
			WB_Vector up = center.add(new WB_Vector(0, 0, 1));
			points.set(i, points.get(i).rotateAboutAxis2P(angle, center, up));
		}
	}

	/**
	 * ��������
	 */
	public void draw() {
		List<WB_Polygon> polygons = getAllCellPolygons();
		List<Cell> cells = getAllCells();
		app.pushStyle();
		app.noFill();
		render.drawPolygonEdges(polygons);
		app.popStyle();
		drawStatus(cells, polygons);
	}

	/**
	 * ����ÿ������ϸ����״̬
	 * 
	 * @param cells
	 * @param polygons
	 */
	private void drawStatus(List<Cell> cells, List<WB_Polygon> polygons) {
		Number number = new Number(app);
		number.set(0.5);
		for (int i = 0; i < cells.size(); i++) {
			int status = cells.get(i).getStatus();
			WB_Point center = polygons.get(i).getCenter();
			number.draw(center.coords(), status);
		}
	}

	/**
	 * �趨������Ϊi��������Ϊj������Ļ�Ծ״̬
	 * 
	 * @param i      ������
	 * @param j      ������
	 * @param status ������µĻ�Ծ״̬
	 */
	public void setStatus(int i, int j, int status) {
		Cell cell = cells.get(i, j);
		cell.setStatus(status);
	}

	/**
	 * ��ȡ�������ϸ����λ��
	 * 
	 * @return
	 */
	public Cell getRandomCell() {
		List<Cell> alive = getAliveCells();
		int num = (int) ((alive.size() - 1) * Math.random());
		return alive.get(num);
	}

	/**
	 * ��ȡ������Ŀ��
	 * 
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * ��ȡ������ĸ߶�
	 * 
	 * @return
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * ��ȡ����Ϊ(i,j)������ϸ����Ӧ�Ķ����
	 * 
	 * @param i ������
	 * @param j ������
	 * @return
	 */
	public WB_Polygon getCellPolygon(int i, int j) {
		Cell cell = cells.get(i, j);
		return new WB_Polygon(points.get(cell));
	}

	/**
	 * ��ȡϸ����Ӧ�Ķ����
	 * 
	 * @param cell
	 * @return
	 */
	public WB_Polygon getCellPolygon(Cell cell) {
		return new WB_Polygon(points.get(cell));
	}

	/**
	 * ��ȡ����Ϊ(i,j)������ϸ��
	 * 
	 * @param i ������
	 * @param j ������
	 * @return
	 */
	public Cell getCell(int i, int j) {
		return cells.get(i, j);
	}

	/**
	 * ��ȡ���Ϊserial������ϸ��
	 * 
	 * @param serial
	 * @return
	 */
	public Cell getCell(int[] serial) {
		return cells.get(serial[0], serial[1]);
	}

	/**
	 * ��ȡ����ϸ����Ŀ
	 * 
	 * @return
	 */
	public int getAliveCellsNum() {
		int sum = 0;
		for (int i = 0; i < cells.size(); i++) {
			if (cells.get(i).isAlive())
				sum++;
		}
		return sum;
	}

	/**
	 * ��ȡ���д���ϸ��
	 * 
	 * @return
	 */
	public List<Cell> getAliveCells() {
		List<Cell> alive = new ArrayList<>();
		cells.foreach(e -> {
			if (e.isAlive())
				alive.add(e);
		});
		return alive;
	}

	/**
	 * �趨���е������
	 */
	private void setPoints() {
		for (int i = 0; i < height + 1; i++) {
			for (int j = 0; j < width + 1; j++) {
				WB_Point p = new WB_Point(unit * j, unit * i, 0);
				points.add(p);
			}
		}
	}

	@Override
	public String toString() {
		String str = "";
		for (int i = 0; i < cells.size(); i++) {
			if (cells.get(i).getStatus() == Cell.ALIVE)
				str += cells.get(i).toString();
		}
		return str;
	}
}
