package site;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 二维矩阵对应的集合，可通过横纵坐标调取元素
 * 
 * @author Naturalpowder
 *
 * @param <T>
 */
public class Collection2D<T> {
	List<T> points;
	int offset;

	/**
	 * 初始化设置
	 */
	public Collection2D() {
		points = new ArrayList<>();
	}

	/**
	 * 设置横轴偏移量，即横轴数目
	 * 
	 * @param offset
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * 按顺序添加的元素e
	 * 
	 * @param e
	 */
	public void add(T e) {
		points.add(e);
	}

	/**
	 * 获取所有元素
	 * 
	 * @return
	 */
	public List<T> getAll() {
		return points;
	}

	/**
	 * 获取第i个元素
	 * 
	 * @param i
	 * @return
	 */
	public T get(int i) {
		return points.get(i);
	}

	/**
	 * 设定第i个元素的值
	 * 
	 * @param i
	 * @param ele
	 */
	public void set(int i, T ele) {
		points.set(i, ele);
	}

	/**
	 * 通过横纵轴坐标获取元素
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	public T get(int i, int j) {
		int serial = j * offset + i;
		if (serial >= 0 && serial < points.size())
			return points.get(serial);
		else
			return null;
	}

	/**
	 * 通过细胞的序号在点集中获取相应矩形
	 * 
	 * @param cell
	 * @return
	 */
	public List<T> get(Cell cell) {
		List<T> list = new ArrayList<>();
		for (int i = 0; i < cell.size(); i++) {
			T element = get(cell.get(i)[0], cell.get(i)[1]);
			list.add(element);
		}
		return list;
	}

	/**
	 * 获取元素数目
	 * 
	 * @return
	 */
	public int size() {
		return points.size();
	}

	/**
	 * foreach方法
	 * 
	 * @param action
	 */
	public void foreach(Consumer<T> action) {
		points.forEach(action);
	}
}
