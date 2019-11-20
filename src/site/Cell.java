package site;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 网格细胞各点的序号集合
 * 
 * @author Naturalpowder
 *
 */
public class Cell {
	/**
	 * 引用点的集合
	 */
	private List<int[]> cells;
	/**
	 * 网格的活跃状态
	 */
	private int status = 0;
	/**
	 * 网格活跃状态的静态变量
	 */
	public static int ALIVE = 0, DEAD = 1, OUT = 2, TEMP = 8;
	/**
	 * 该细胞在网格中的编号
	 */
	private int[] serial;

	/**
	 * 初始化
	 */
	public Cell() {
		cells = new ArrayList<int[]>();
	}

	/**
	 * 获取该网格的编号
	 * 
	 * @return
	 */
	public int[] getSerial() {
		serial = cells.get(0);
		return serial;
	}

	/**
	 * 获取网格的活跃状态
	 * 
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 设定网格的活跃状态
	 * 
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 添加坐标为(x,y)的引用点
	 * 
	 * @param x
	 * @param y
	 */
	public void add(int x, int y) {
		cells.add(new int[] { x, y });
	}

	/**
	 * 获取序号为index的点的引用
	 * 
	 * @param index
	 * @return
	 */
	public int[] get(int index) {
		return cells.get(index);
	}

	/**
	 * 包含的点数
	 * 
	 * @return
	 */
	public int size() {
		return cells.size();
	}

	/**
	 * 每个点引用序号的数目(二维点即为2，三维点即为3)
	 * 
	 * @return
	 */
	public int secondSize() {
		if (!cells.isEmpty())
			return cells.get(0).length;
		else
			return -1;
	}

	/**
	 * 获取细胞是否为生，是则返回true
	 * 
	 * @return
	 */
	public boolean isAlive() {
		return status == ALIVE;
	}

	/**
	 * 获取一个细胞周边的所有相邻细胞
	 * 
	 * @param grid
	 * @return
	 */
	public List<Cell> getNeighbours(Grid grid, boolean original) {
		serial = this.cells.get(0);
		List<Cell> cells = new ArrayList<>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if ((i == 0 && j != 0) || (j == 0 && i != 0)) {
					Cell cell = grid.getCell(i + serial[0], j + serial[1]);
					if (!original) {
						if (cell != null && cell.isAlive())
							cells.add(cell);
					} else if (cell != null && cell.getStatus() == OUT)
						cells.add(cell);
				}
			}
		}
		return cells;
	}

	/**
	 * 获取本细胞位置减去另一个细胞位置得到的细胞
	 * 
	 * @param grid
	 * @param other
	 * @return
	 */
	public Cell minus(Grid grid, Cell other, int offset) {
		int[] b = other.getSerial();
		serial = getSerial();
		Cell cell = grid.getCell(serial[0] - b[0] + offset, serial[1] - b[1] + offset);
		return cell;
	}

	@Override
	public String toString() {
		String str = "";
		serial = cells.get(0);
		str += Arrays.toString(serial);
		return str;
	}

}
