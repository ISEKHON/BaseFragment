package xyz.illuminate.ui.Models;

import xyz.illuminate.ui.ActionBar.Theme;

public class ChipItem {
    private int chipBackgroundColor;
    private int chipStrokeColor;
    private int chipTextColor;
    private int chipIcon;
    private String chipKey;
    private String chipText;

    public ChipItem(String chipText) {
        this.chipText = chipText;
    }

    public ChipItem(String key, String chipText) {
        this.chipKey = key;
        this.chipText = chipText;
    }

    public ChipItem(String chipText, int chipIcon) {
        this.chipText = chipText;
        this.chipIcon = chipIcon;
    }

    public ChipItem(String key, String chipText, int chipIcon, int chipBackgroundColor, int chipTextColor) {
        this.chipKey = key;
        this.chipText = chipText;
        this.chipBackgroundColor = chipBackgroundColor;
        this.chipIcon = chipIcon;
        this.chipTextColor = chipTextColor;
    }

    public ChipItem(String key, String chipText, int chipIcon, int chipBackgroundColor, int chipTextColor, int stroke) {
        this.chipKey = key;
        this.chipText = chipText;
        this.chipBackgroundColor = chipBackgroundColor;
        this.chipIcon = chipIcon;
        this.chipTextColor = chipTextColor;
        this.chipStrokeColor = stroke;
    }

    public String getChipText() {
        return chipText;
    }

    public String getChipKey() {
        return chipKey;
    }

    public int getChipIcon() {
        return chipIcon;
    }

    public int getChipTextColor() {
        if (chipTextColor == 0) {
            return Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2);
        }
        return chipTextColor;
    }

    public int getChipBackgroundColor() {
        if (chipBackgroundColor == 0) {
            return Theme.getColor(Theme.key_windowBackgroundWhite);
        }
        return chipBackgroundColor;
    }

    public int getChipStrokeColor() {
        if (chipStrokeColor == 0) {
            Theme.getColor(Theme.key_outlineColor);
        }
        return chipStrokeColor;
    }
}
