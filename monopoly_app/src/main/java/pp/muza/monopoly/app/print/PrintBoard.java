package pp.muza.monopoly.app.print;

import static pp.muza.formatter.Meta.LINES_SEPARATOR;
import static pp.muza.monopoly.app.I18n.resourceBundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;

import pp.muza.formatter.AsciiCanvas;
import pp.muza.formatter.LineFormatter;
import pp.muza.monopoly.app.I18n;
import pp.muza.monopoly.model.Board;
import pp.muza.monopoly.model.Land;
import pp.muza.monopoly.model.Property;
import pp.muza.monopoly.model.pieces.lands.LandType;

public class PrintBoard {

    public static final int WIDTH = 13;
    public static final int HEIGHT = 5;
    public static final int PADDING_W = 2;
    public static final int PADDING_H = 1;
    public static boolean isColorMode = true;
    private static final Map<String, AnsiFormat> colorMap;

    static {
        colorMap = new HashMap<>();
        colorMap.put("GREEN", new AnsiFormat(Attribute.BLACK_TEXT(), Attribute.BRIGHT_GREEN_BACK()));
        colorMap.put("INDIGO", new AnsiFormat(Attribute.BLACK_TEXT(), Attribute.BRIGHT_BLUE_BACK()));
        colorMap.put("ORANGE", new AnsiFormat(Attribute.BLACK_TEXT(), Attribute.YELLOW_BACK()));
        colorMap.put("RAINBOW", new AnsiFormat(Attribute.MAGENTA_TEXT(), Attribute.BRIGHT_CYAN_BACK()));
        colorMap.put("RED", new AnsiFormat(Attribute.BLACK_TEXT(), Attribute.RED_BACK()));
        colorMap.put("VIOLET", new AnsiFormat(Attribute.BLACK_TEXT(), Attribute.BRIGHT_MAGENTA_BACK()));
        colorMap.put("YELLOW", new AnsiFormat(Attribute.BLACK_TEXT(), Attribute.BRIGHT_YELLOW_BACK()));
        colorMap.put("BLUE", new AnsiFormat(Attribute.BRIGHT_CYAN_TEXT(), Attribute.BLUE_BACK()));
        colorMap.put("START", new AnsiFormat(Attribute.BLACK_TEXT(), Attribute.BRIGHT_GREEN_BACK(), Attribute.BOLD()));
        colorMap.put("JAIL", new AnsiFormat(Attribute.BLACK_TEXT(), Attribute.BRIGHT_RED_BACK(), Attribute.BOLD()));
        colorMap.put("PARKING",
                new AnsiFormat(Attribute.BLACK_TEXT(), Attribute.BRIGHT_YELLOW_BACK(), Attribute.BOLD()));
        colorMap.put("GOTO_JAIL",
                new AnsiFormat(Attribute.BRIGHT_WHITE_TEXT(), Attribute.BRIGHT_RED_BACK(), Attribute.BOLD()));
        colorMap.put("CHANCE", new AnsiFormat(Attribute.BLACK_TEXT(), Attribute.BRIGHT_WHITE_BACK(), Attribute.BOLD()));
    }

    private static String landText(Land land) {
        StringBuilder sb = new StringBuilder();
        if (land.getType() == LandType.PROPERTY) {
            sb.append(resourceBundle.getString(((Property) land).getColor().name()).toUpperCase(I18n.currentLocale))
                    .append(LINES_SEPARATOR);
            sb.append(resourceBundle.getString(land.getName()));
            sb.append(" $").append(((Property) land).getPrice()).append(LINES_SEPARATOR);
        } else {
            sb.append(resourceBundle.getString(land.getName()));
        }
        return sb.toString();
    }

    public static String printBoard(Board board) {
        int size = 24;
        assert size == board.size();
        int width = 7;
        int height = 7;

        // create a canvas
        AsciiCanvas canvas = new AsciiCanvas(width * (WIDTH - 1) + 1 + 2 * PADDING_W,
                height * (HEIGHT - 1) + 1 + 2 * PADDING_H);
        canvas.setColorMode(isColorMode);

        // start from the bottom-right corner
        int left = width - 1;
        int top = height - 1;
        int vx = -1;
        int vy = 0;

        int px = 0;
        int py = 0;

        // for storing the positions of the lands
        Collection<Cell> cells = new ArrayList<>();
        var border = LineFormatter.Border.ALL;
        for (int i = 0; i < size; i++) {
            Land l = board.getLand(i);
            AnsiFormat color;
            if (l instanceof Property p) {
                color = colorMap.get(p.getColor().name());
            } else {
                color = colorMap.get(l.getType().name());
            }
            String text = landText(l);

            List<String> block = LineFormatter.textRectangle(WIDTH + px, HEIGHT + py, text, border,
                    AsciiCanvas.SPACE_CHAR);
            int left1 = left * (WIDTH - 1);
            int top1 = top * (HEIGHT - 1);
            Cell c = new Cell(block, PADDING_W + left1, PADDING_H + top1, i, color);
            cells.add(c);

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
            left += vx;
            top += vy;
        }

        // sort the cells by from top to bottom, left to right
        cells.stream()
                .sorted(Comparator.comparingInt(c -> c.top))
                .sorted(Comparator.comparingInt(c -> c.left))
                .forEach(c -> {
                    canvas.setColor(c.color);
                    canvas.pasteLines(c.left, c.top, c.block);
                    canvas.setColor(canvas.getColorAt(c.left, c.top));
                    canvas.drawText(c.left + 1, c.top, "[" + c.index + "]");
                });

        // point borders
        int realWidth = canvas.getWidth();
        int realHeight = canvas.getHeight();
        canvas.setColor(new AnsiFormat(Attribute.BRIGHT_GREEN_TEXT(), Attribute.GREEN_BACK()));
        canvas.drawText(0, 0, "+");
        canvas.drawText(realWidth - 1, 0, "+");
        canvas.drawText(0, realHeight - 1, "+");
        canvas.drawText(realWidth - 1, realHeight - 1, "+");

        return canvas.toString();
    }

    private record Cell(List<String> block, int left, int top, int index, AnsiFormat color) {
    }
}
