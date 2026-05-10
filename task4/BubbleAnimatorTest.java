import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BubbleAnimatorTest {

    @Test
    void shouldSortArray() {
        BubbleAnimator animator = new BubbleAnimator();
        animator.reset(new int[]{5, 3, 1, 4, 2});

        int limit = 1000;
        while (!animator.isFinished() && limit-- > 0) {
            animator.step();
        }

        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, animator.getArray());
    }

    @Test
    void shouldCountOperations() {
        BubbleAnimator animator = new BubbleAnimator();
        animator.reset(new int[]{3, 2, 1});

        int limit = 1000;
        while (!animator.isFinished() && limit-- > 0) {
            animator.step();
        }

        assertTrue(animator.getComparisons() > 0);
        assertTrue(animator.getSwaps() > 0);
    }

    @Test
    void shouldResetState() {
        BubbleAnimator animator = new BubbleAnimator();
        animator.reset(new int[]{3, 2, 1});

        animator.step();
        animator.step();

        animator.reset(new int[]{9, 8, 7});

        assertArrayEquals(new int[]{9, 8, 7}, animator.getArray());
        assertEquals(0, animator.getComparisons());
        assertEquals(0, animator.getSwaps());
        assertFalse(animator.isFinished());
    }
}