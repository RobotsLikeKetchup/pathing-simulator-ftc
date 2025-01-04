package pathingSim;

public class Ticker {
    int count = 0;

    public Ticker() {}

    public int getCount() {
        return count;
    }

    public void up() {
        count++;
    }

    public void down() {
        count--;
    }

    public void setCount(int num) {
        count = num;
    }
}
