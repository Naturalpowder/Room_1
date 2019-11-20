package site;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ����ϸ���������ż���
 * 
 * @author Naturalpowder
 *
 */
public class Cell {
	/**
	 * ���õ�ļ���
	 */
	private List<int[]> cells;
	/**
	 * ����Ļ�Ծ״̬
	 */
	private int status = 0;
	/**
	 * �����Ծ״̬�ľ�̬����
	 */
	public static int ALIVE = 0, DEAD = 1, OUT = 2, TEMP = 8;
	/**
	 * ��ϸ���������еı��
	 */
	private int[] serial;

	/**
	 * ��ʼ��
	 */
	public Cell() {
		cells = new ArrayList<int[]>();
	}

	/**
	 * ��ȡ������ı��
	 * 
	 * @return
	 */
	public int[] getSerial() {
		serial = cells.get(0);
		return serial;
	}

	/**
	 * ��ȡ����Ļ�Ծ״̬
	 * 
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * �趨����Ļ�Ծ״̬
	 * 
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * �������Ϊ(x,y)�����õ�
	 * 
	 * @param x
	 * @param y
	 */
	public void add(int x, int y) {
		cells.add(new int[] { x, y });
	}

	/**
	 * ��ȡ���Ϊindex�ĵ������
	 * 
	 * @param index
	 * @return
	 */
	public int[] get(int index) {
		return cells.get(index);
	}

	/**
	 * �����ĵ���
	 * 
	 * @return
	 */
	public int size() {
		return cells.size();
	}

	/**
	 * ÿ����������ŵ���Ŀ(��ά�㼴Ϊ2����ά�㼴Ϊ3)
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
	 * ��ȡϸ���Ƿ�Ϊ�������򷵻�true
	 * 
	 * @return
	 */
	public boolean isAlive() {
		return status == ALIVE;
	}

	/**
	 * ��ȡһ��ϸ���ܱߵ���������ϸ��
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
	 * ��ȡ��ϸ��λ�ü�ȥ��һ��ϸ��λ�õõ���ϸ��
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
