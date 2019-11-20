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
 * 创建包含引用关系的网格
 * 
 * @author Naturalpowder
 *
 */
public class Grid {
	/**
	 * 网格宽度方向的格子数目
	 */
	private int width;
	/**
	 * 网格高度方向的格子数目
	 */
	private int height;
	/**
	 * 网格单元的边长
	 */
	private double unit;
	/**
	 * 网格点的集合
	 */
	private Collection2D<WB_Point> points;
	/**
	 * 所有细胞的集合
	 */
	private Collection2D<Cell> cells;
	/**
	 * 网格的原点坐标
	 */
	private WB_Point origin;
	/**
	 * 渲染器
	 */
	private WB_Render render;
	/**
	 * Processing类
	 */
	PApplet app;

	/**
	 * 初始化类
	 */
	public Grid() {
	}

	/**
	 * 初始化网格
	 * 
	 * @param width  x方向网格数目
	 * @param height y方向网格数目
	 * @param unit   单元边长
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
	 * 获取网格最外层边界多边形
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
	 * 设定初始数据
	 * 
	 * @param origin 原点
	 * @param app    Processing类
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
	 * 以现在的原点为移动后网格的中心点进行移动网格操作
	 */
	public void centerMove() {
		WB_Point center = getSite().getCenter();
		move(origin.sub(center));
	}

	/**
	 * 移动网格
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
	 * 设置网格细胞对点的引用关系
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
	 * 获取边界内所有的网格细胞对应的多边形
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
	 * 获取边界内所有的网格细胞
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
	 * 以一定边界对网格进行裁剪，获取裁剪后只在边界内的网格
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
	 * 将网格按一定角度进行旋转
	 * 
	 * @param angle 旋转角度
	 */
	public void rotate(double angle) {
		WB_Point center = getSite().getCenter();
		for (int i = 0; i < points.size(); i++) {
			WB_Vector up = center.add(new WB_Vector(0, 0, 1));
			points.set(i, points.get(i).rotateAboutAxis2P(angle, center, up));
		}
	}

	/**
	 * 绘制网格
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
	 * 绘制每个网格细胞的状态
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
	 * 设定横坐标为i，纵坐标为j的网格的活跃状态
	 * 
	 * @param i      横坐标
	 * @param j      纵坐标
	 * @param status 网格的新的活跃状态
	 */
	public void setStatus(int i, int j, int status) {
		Cell cell = cells.get(i, j);
		cell.setStatus(status);
	}

	/**
	 * 获取随机网格细胞的位置
	 * 
	 * @return
	 */
	public Cell getRandomCell() {
		List<Cell> alive = getAliveCells();
		int num = (int) ((alive.size() - 1) * Math.random());
		return alive.get(num);
	}

	/**
	 * 获取该网格的宽度
	 * 
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * 获取该网格的高度
	 * 
	 * @return
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * 获取坐标为(i,j)的网格细胞对应的多边形
	 * 
	 * @param i 横坐标
	 * @param j 纵坐标
	 * @return
	 */
	public WB_Polygon getCellPolygon(int i, int j) {
		Cell cell = cells.get(i, j);
		return new WB_Polygon(points.get(cell));
	}

	/**
	 * 获取细胞对应的多边形
	 * 
	 * @param cell
	 * @return
	 */
	public WB_Polygon getCellPolygon(Cell cell) {
		return new WB_Polygon(points.get(cell));
	}

	/**
	 * 获取坐标为(i,j)的网格细胞
	 * 
	 * @param i 横坐标
	 * @param j 纵坐标
	 * @return
	 */
	public Cell getCell(int i, int j) {
		return cells.get(i, j);
	}

	/**
	 * 获取序号为serial的网格细胞
	 * 
	 * @param serial
	 * @return
	 */
	public Cell getCell(int[] serial) {
		return cells.get(serial[0], serial[1]);
	}

	/**
	 * 获取存活的细胞数目
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
	 * 获取所有存活的细胞
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
	 * 设定所有点的坐标
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
