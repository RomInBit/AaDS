
public abstract class AbstractAnimator {
    protected int[] array = new int[0];
    protected int comparisons;
    protected int swaps;
    protected int highlightA = -1;
    protected int highlightB = -1;
    protected boolean finished = true;

    public void reset(int[] source) {
        this.array = source.clone();
        this.comparisons = 0;
        this.swaps = 0;
        this.highlightA = -1;
        this.highlightB = -1;
        this.finished = array.length <= 1;
    }

    public int[] getArray() {
        return array;
    }

    public long getComparisons() {
        return comparisons;
    }

    public long getSwaps() {
        return swaps;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isHighlighted(int index) {
        return index == highlightA || index == highlightB;
    }

    public abstract boolean isSorted(int index);

    protected void swap(int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
        swaps++;
    }

    protected void clearHighlights() {
        highlightA = -1;
        highlightB = -1;
    }

    public abstract void step();
}