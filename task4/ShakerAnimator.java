
public class ShakerAnimator extends AbstractAnimator {
    private int left;
    private int right;
    private int i;
    private boolean forward;
    private boolean swappedInPass;

    @Override
    public void reset(int[] source) {
        super.reset(source);
        this.left = 0;
        this.right = array.length - 1;
        this.i = 0;
        this.forward = true;
        this.swappedInPass = false;
        this.finished = array.length <= 1;
    }

    @Override
    public boolean isSorted(int index) {
        return finished || index < left || index > right;
    }

    @Override
    public void step() {
        if (finished) {
            clearHighlights();
            return;
        }

        if (left >= right) {
            finished = true;
            clearHighlights();
            return;
        }

        if (forward) {
            if (i >= right) {
                right--;
                if (!swappedInPass || left >= right) {
                    finished = true;
                    clearHighlights();
                    return;
                }
                forward = false;
                i = right;
                swappedInPass = false;
                clearHighlights();
                return;
            }

            highlightA = i;
            highlightB = i + 1;
            comparisons++;

            if (array[i] > array[i + 1]) {
                swap(i, i + 1);
                swappedInPass = true;
            }

            i++;
        } else {
            if (i <= left) {
                left++;
                if (!swappedInPass || left >= right) {
                    finished = true;
                    clearHighlights();
                    return;
                }
                forward = true;
                i = left;
                swappedInPass = false;
                clearHighlights();
                return;
            }

            highlightA = i - 1;
            highlightB = i;
            comparisons++;

            if (array[i - 1] > array[i]) {
                swap(i - 1, i);
                swappedInPass = true;
            }

            i--;
        }
    }
}