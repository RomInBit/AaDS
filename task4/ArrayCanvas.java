
import javax.swing.*;
import java.awt.*;

public class ArrayCanvas extends JPanel {
    private final AbstractAnimator animator;

    public ArrayCanvas(AbstractAnimator animator) {
        this.animator = animator;
        setPreferredSize(new Dimension(550, 240));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int[] array = animator.getArray();
        if (array == null || array.length == 0) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int n = array.length;

        int cellGap = 6;
        int topPadding = 30;
        int bottomPadding = 25;
        int usableHeight = height - topPadding - bottomPadding;
        int cellWidth = Math.max(20, (width - (n + 1) * cellGap) / n);
        int cellHeight = Math.max(44, usableHeight);

        int x = cellGap;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));

        for (int i = 0; i < n; i++) {
            int y = topPadding + (usableHeight - cellHeight) / 2;

            Color fill;
            if (animator.isHighlighted(i)) {
                fill = new Color(255, 235, 59);
            } else if (animator.isSorted(i)) {
                fill = new Color(190, 190, 190);
            } else {
                fill = Color.WHITE;
            }

            g2.setColor(fill);
            g2.fillRoundRect(x, y, cellWidth, cellHeight, 10, 10);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(x, y, cellWidth, cellHeight, 10, 10);

            String value = String.valueOf(array[i]);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(value);
            int textX = x + (cellWidth - textWidth) / 2;
            int textY = y + (cellHeight + fm.getAscent()) / 2 - 4;
            g2.drawString(value, textX, textY);

            x += cellWidth + cellGap;
        }

        g2.dispose();
    }
}