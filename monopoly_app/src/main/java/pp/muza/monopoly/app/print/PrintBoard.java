package pp.muza.monopoly.app.print;

import pp.muza.formatter.AsciiCanvas;
import pp.muza.formatter.LineFormatter;
import pp.muza.monopoly.app.I18n;
import pp.muza.monopoly.model.Board;
import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.Property;
import pp.muza.monopoly.model.pieces.lands.LandType;

import java.util.List;

import static pp.muza.monopoly.app.I18n.resourceBundle;

public class PrintBoard {

    public static final int WIDTH = 13;
    public static final int HEIGHT = 5;

    private static String landText(Land land) {
        StringBuilder sb = new StringBuilder();
        if (land.getType() == LandType.PROPERTY) {
            sb.append(resourceBundle.getString(((Property) land).getColor().name()).toUpperCase(I18n.currentLocale)).append("\n");
            sb.append(resourceBundle.getString(land.getName()));
            sb.append(" $").append(((Property) land).getPrice()).append("\n");
        } else {
            sb.append(resourceBundle.getString(land.getName()));
        }
        return sb.toString();
    }

    public static String printBoard(Board board) {
        int size = board.size();
        int width = (int) Math.ceil(size / 4.0) + 1;
        int height = (int) Math.ceil(size / 4.0) + 1;

        // create a canvas
        AsciiCanvas canvas = new AsciiCanvas(width * (WIDTH - 1) + 1, height * (HEIGHT - 1) + 1);

        // start from the bottom-right corner
        int left = width - 1;
        int top = height - 1;
        int vx = -1;
        int vy = 0;

        for (int i = 0; i < size; i++) {
            String text = landText(board.getLand(i));
            List<String> block = LineFormatter.textRectangle(WIDTH, HEIGHT, text, LineFormatter.Border.ALL, ' ');
            canvas.pasteLines(left * (WIDTH - 1), top * (HEIGHT - 1), block);
            left += vx;
            top += vy;
            if (left == 0 && top == (height - 1)) {
                vx = 0;
                vy = -1;
            } else if (top == 0 && left == 0) {
                vx = 1;
                vy = 0;
            } else if (left == width - 1 && top == 0) {
                vx = 0;
                vy = 1;
            } else if (top == height - 1 && left == width - 1) {
                vx = -1;
                vy = 0;
            }
        }
        return canvas.toString();
    }
}
