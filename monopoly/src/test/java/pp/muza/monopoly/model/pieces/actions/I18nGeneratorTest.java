package pp.muza.monopoly.model.pieces.actions;

import org.apache.commons.text.WordUtils;
import org.junit.jupiter.api.Test;
import pp.muza.monopoly.errors.BankError;
import pp.muza.monopoly.errors.GameError;
import pp.muza.monopoly.errors.TurnError;
import pp.muza.monopoly.model.Asset;
import pp.muza.monopoly.model.PropertyColor;
import pp.muza.monopoly.model.pieces.lands.LandType;

public class I18nGeneratorTest {

    static String normalizeValue(String s) {
        return WordUtils.capitalize(s.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase().replaceAll("_", " "));
    }

    @Test
    public void i18nGeneration() {
        System.out.println("# Colors");
        PropertyColor[] colors = PropertyColor.values();
        for (PropertyColor value : colors) {
            System.out.println(value.name() + "=" + normalizeValue(value.name()));
        }
        System.out.println("# Assets");
        Asset[] assets = Asset.values();
        for (Asset value : assets) {
            System.out.println(value.name() + "=" + normalizeValue(value.name()));
        }
        System.out.println("# Land types");
        LandType[] landTypes = LandType.values();
        for (LandType value : landTypes) {
            System.out.println(value.name() + "=" + normalizeValue(value.name()));
        }
        System.out.println("# Land names");
        for (LandType value : LandType.values()) {
            System.out.println(value.getAClass().getSimpleName() + "=" + normalizeValue(value.getAClass().getSimpleName()));
        }
        Action[] actions = Action.values();
        System.out.println("# Actions");
        for (Action value : actions) {
            for (Class<? extends BaseActionCard> clazz : value.getClassList()) {
                System.out.println(clazz.getSimpleName() + "=" + normalizeValue(clazz.getSimpleName()));
            }
        }
        System.out.println("# Chances");
        Chance[] chances = Chance.values();
        for (Chance value : chances) {
            System.out.println(value.name() + "=" + normalizeValue(value.name()));
        }
        System.out.println("# Errors");
        BankError[] errors = BankError.values();
        for (BankError value : errors) {
            System.out.println(value.name() + "=" + value.getMessage());
        }
        TurnError[] turnErrors = TurnError.values();
        for (TurnError value : turnErrors) {
            System.out.println(value.name() + "=" + value.getMessage());
        }
        GameError[] gameErrors = GameError.values();
        for (GameError value : gameErrors) {
            System.out.println(value.name() + "=" + value.getMessage());
        }
    }
}
