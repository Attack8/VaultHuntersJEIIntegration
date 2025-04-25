package dev.attackeight.just_enough_vh.utils;

public class SlotPlacer {

    public static int getX(int index) {
        return switch (index) {
            case 3,4,5 -> 20;
            case 6,7,8 -> 38;
            default -> 2;
        };
    }

    public static int getY(int index) {
        return switch (index) {
            case 1,4,7 -> 20;
            case 2,5,8 -> 38;
            default -> 2;
        };
    }
}
