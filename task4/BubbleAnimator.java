
public class BubbleAnimator extends AbstractAnimator {
    private int i;
    private int j;
    private boolean swappedInPass;
    private int sortedTailStart;

    @Override
    public void reset(int[] source) {
        super.reset(source);
        this.i = 0;
        this.j = 0;
        this.swappedInPass = false;
        this.sortedTailStart = array.length;
        this.finished = array.length <= 1;
        if (finished) {
            sortedTailStart = 0;
        }
    }

    @Override
    public boolean isSorted(int index) {
        return finished || index >= sortedTailStart;
    }

    @Override
    public void step() {
        if (finished) {
            clearHighlights();
            return;
        }

        int n = array.length;

        if (i >= n - 1) {
            finished = true;
            sortedTailStart = 0;
            clearHighlights();
            return;
        }

        if (j >= n - i - 1) {
            if (!swappedInPass) {
                finished = true;
                sortedTailStart = 0;
            } else {
                i++;
                j = 0;
                swappedInPass = false;
                sortedTailStart = n - i;
            }
            clearHighlights();
            return;
        }

        highlightA = j;
        highlightB = j + 1;
        comparisons++;

        if (array[j] > array[j + 1]) {
            swap(j, j + 1);
            swappedInPass = true;
        }

        j++;
        sortedTailStart = n - i;
    }
}