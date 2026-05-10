import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShakerAnimatorTest {

    @Test
    void shouldSortArray() {
        ShakerAnimator animator = new ShakerAnimator();
        animator.reset(new int[]{5, 3, 1, 4, 2});

        int limit = 1000;
        while (!animator.isFinished() && limit-- > 0) {
            animator.step();
        }

        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, animator.getArray());
    }

    @Test
    void shouldCountOperations() {
        ShakerAnimator animator = new ShakerAnimator();
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
        ShakerAnimator animator = new ShakerAnimator();
        animator.reset(new int[]{4, 1, 3});

        animator.step();

        animator.reset(new int[]{7, 6, 5});

        assertArrayEquals(new int[]{7, 6, 5}, animator.getArray());
        assertEquals(0, animator.getComparisons());
        assertEquals(0, animator.getSwaps());
        assertFalse(animator.isFinished());
    }
}