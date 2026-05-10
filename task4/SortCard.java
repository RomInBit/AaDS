
import javax.swing.*;
import java.awt.*;

public class SortCard extends JPanel {
    private final AbstractAnimator animator;
    private final ArrayCanvas canvas;
    private final JLabel comparisonsLabel;
    private final JLabel swapsLabel;
    private final JLabel stateLabel;

    public SortCard(String title, AbstractAnimator animator) {
        this.animator = animator;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(8, 8, 8, 8),
                BorderFactory.createLineBorder(Color.DARK_GRAY)
        ));

        JLabel header = new JLabel(title, SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
        add(header, BorderLayout.NORTH);

        canvas = new ArrayCanvas(animator);
        add(canvas, BorderLayout.CENTER);

        JPanel info = new JPanel(new GridLayout(3, 1, 4, 4));
        comparisonsLabel = new JLabel();
        swapsLabel = new JLabel();
        stateLabel = new JLabel();

        info.add(comparisonsLabel);
        info.add(swapsLabel);
        info.add(stateLabel);
        add(info, BorderLayout.SOUTH);

        refresh();
    }

    public void refresh() {
        comparisonsLabel.setText("Сравнения: " + animator.getComparisons());
        swapsLabel.setText("Перестановки: " + animator.getSwaps());
        stateLabel.setText(animator.isFinished() ? "Состояние: завершено" : "Состояние: выполняется");
        canvas.repaint();
    }
}