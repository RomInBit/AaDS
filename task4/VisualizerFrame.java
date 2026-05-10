import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class VisualizerFrame extends JFrame {
    private static final int DEFAULT_SIZE = 20;
    private static final int DEFAULT_DELAY = 250;

    private final JSpinner sizeSpinner;
    private final JSlider speedSlider;
    private final JButton startPauseButton;
    private final JButton resetButton;
    private final JLabel statusLabel;

    private final BubbleAnimator bubbleAnimator = new BubbleAnimator();
    private final ShakerAnimator shakerAnimator = new ShakerAnimator();

    private final SortCard bubbleCard;
    private final SortCard shakerCard;

    private final Timer timer;
    private boolean running = false;

    public VisualizerFrame() {
        super("Сравнение пузырьковой и шейкерной сортировки");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Пошаговая визуализация сортировок", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        add(title, BorderLayout.NORTH);

        bubbleCard = new SortCard("Пузырьковая сортировка", bubbleAnimator);
        shakerCard = new SortCard("Шейкерная сортировка", shakerAnimator);

        JPanel center = new JPanel(new GridLayout(1, 2, 12, 12));
        center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        center.add(bubbleCard);
        center.add(shakerCard);
        add(center, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));

        sizeSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_SIZE, 5, 40, 1));

        speedSlider = new JSlider(50, 1000, DEFAULT_DELAY);
        speedSlider.setPreferredSize(new Dimension(220, 45));
        speedSlider.setMajorTickSpacing(250);
        speedSlider.setMinorTickSpacing(50);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);

        startPauseButton = new JButton("Старт");
        resetButton = new JButton("Сгенерировать массивы");
        statusLabel = new JLabel("Готово к запуску");

        controls.add(new JLabel("Размер массива:"));
        controls.add(sizeSpinner);
        controls.add(new JLabel("Скорость:"));
        controls.add(speedSlider);
        controls.add(startPauseButton);
        controls.add(resetButton);
        controls.add(statusLabel);

        add(controls, BorderLayout.SOUTH);

        timer = new Timer(speedSlider.getValue(), e -> onTick());

        startPauseButton.addActionListener(e -> toggleRunning());
        resetButton.addActionListener(e -> resetSimulation());
        speedSlider.addChangeListener(e -> timer.setDelay(speedSlider.getValue()));

        resetSimulation();

        setSize(1250, 480);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void toggleRunning() {
        if (!running) {
            running = true;
            timer.setDelay(speedSlider.getValue());
            timer.start();
            startPauseButton.setText("Пауза");
            statusLabel.setText("Сортировка идёт...");
        } else {
            running = false;
            timer.stop();
            startPauseButton.setText("Старт");
            statusLabel.setText("Пауза");
        }
    }

    private void resetSimulation() {
        timer.stop();
        running = false;
        startPauseButton.setText("Старт");

        int size = (Integer) sizeSpinner.getValue();
        int[] baseArray = generateRandomArray(size);

        bubbleAnimator.reset(baseArray);
        shakerAnimator.reset(baseArray);

        bubbleCard.refresh();
        shakerCard.refresh();
        statusLabel.setText("Массивы сгенерированы");
    }

    private void onTick() {
        bubbleAnimator.step();
        shakerAnimator.step();

        bubbleCard.refresh();
        shakerCard.refresh();

        if (bubbleAnimator.isFinished() && shakerAnimator.isFinished()) {
            timer.stop();
            running = false;
            startPauseButton.setText("Старт");
            statusLabel.setText("Сортировка завершена");
        }
    }

    private int[] generateRandomArray(int size) {
        Random random = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(90) + 10;
        }
        return array;
    }
}