package site;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * ��ά�����Ӧ�ļ��ϣ���ͨ�����������ȡԪ��
 * 
 * @author Naturalpowder
 *
 * @param <T>
 */
public class Collection2D<T> {
	List<T> points;
	int offset;

	/**
	 * ��ʼ������
	 */
	public Collection2D() {
		points = new ArrayList<>();
	}

	/**
	 * ���ú���ƫ��������������Ŀ
	 * 
	 * @param offset
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * ��˳����ӵ�Ԫ��e
	 * 
	 * @param e
	 */
	public void add(T e) {
		points.add(e);
	}

	/**
	 * ��ȡ����Ԫ��
	 * 
	 * @return
	 */
	public List<T> getAll() {
		return points;
	}

	/**
	 * ��ȡ��i��Ԫ��
	 * 
	 * @param i
	 * @return
	 */
	public T get(int i) {
		return points.get(i);
	}

	/**
	 * �趨��i��Ԫ�ص�ֵ
	 * 
	 * @param i
	 * @param ele
	 */
	public void set(int i, T ele) {
		points.set(i, ele);
	}

	/**
	 * ͨ�������������ȡԪ��
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
	 * ͨ��ϸ��������ڵ㼯�л�ȡ��Ӧ����
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
	 * ��ȡԪ����Ŀ
	 * 
	 * @return
	 */
	public int size() {
		return points.size();
	}

	/**
	 * foreach����
	 * 
	 * @param action
	 */
	public void foreach(Consumer<T> action) {
		points.forEach(action);
	}
}
