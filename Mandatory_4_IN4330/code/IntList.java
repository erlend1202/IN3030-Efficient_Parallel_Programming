
public class IntList {
  int[] data;
  int len = 0;

  IntList(int len) {
    data = new int[Math.max(2, len)];
  }

  IntList() {
    data = new int[16];
  }

  void add(int elem) {
    if (len == data.length) {
      int b[] = new int[data.length * 2];
      System.arraycopy(data, 0, b, 0, len);
      data = b;
    }
    data[len++] = elem;
  }

  // adds element at given position. if position is larger than position of
  // last element it adds it at the end of the list.
  void add(int elem, int pos) {
    if (pos >= len) add(elem);
    else data[pos] = elem;
  }

  void append(IntList other) {
    if (len + other.len > data.length) {
      int newLen = Math.max(2 * len, len + 2 * other.len);
      int[] b = new int[newLen];
      System.arraycopy(data, 0, b, 0, len);
      data = b;
    }

    System.arraycopy(other.data, 0, data, len, other.len);
    len += other.len;
  }

  void clear() {
    len = 0;
  }

  int get(int pos) {
    if (pos > len - 1) return -1; else return data[pos];
  }

  int getAndRemoveLast() {
    len--; return data[len];
  }

  // Not necessary when data and length aren't private...
  int size() {
    return len;
  }

  int[] returnData(){
    return data;
  }

  void print() {
    for (int i = 0; i < len; i++) {
      if (i % 15 == 0) System.out.println("");

      System.out.print(data[i] + "\t");
    }
    System.out.println("");
  }
}
