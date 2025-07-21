package busbooking.ui.utils;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

public interface UIConstants {

    Color COLOR_PRIMARY_ACTION = new Color(0x472FD2);

    Color COLOR_BACKGROUND = Color.WHITE;
    Color COLOR_HEADER_BACKGROUND = COLOR_PRIMARY_ACTION;

    Color COLOR_TEXT_DARK_PRIMARY = new Color(0x1A202C);
    Color COLOR_TEXT_DARK_SECONDARY = new Color(0x2D3748);
    Color COLOR_TEXT_LIGHT_PRIMARY = new Color(0x4A5568);
    Color COLOR_TEXT_LIGHT_SECONDARY = new Color(0x718096);
    Color COLOR_TEXT_ON_PRIMARY_ACTION = Color.WHITE;

    Color COLOR_BUTTON_TEXT_WHITE = Color.WHITE;
    Color COLOR_BORDER_LIGHT = new Color(0xE2E8F0);
    Color COLOR_ERROR_RED = new Color(0xE53E3E);
    Color COLOR_SUCCESS_GREEN = new Color(0x38A169);

    Font FONT_HEADING_H1 = new Font("SansSerif", Font.BOLD, 28);
    Font FONT_HEADING_H2 = new Font("SansSerif", Font.BOLD, 24);
    Font FONT_HEADING_H3 = new Font("SansSerif", Font.BOLD, 20);

    Font FONT_BODY_BOLD = new Font("SansSerif", Font.BOLD, 16);
    Font FONT_BODY_PLAIN = new Font("SansSerif", Font.PLAIN, 16);

    Font FONT_BUTTON_PRIMARY = new Font("SansSerif", Font.BOLD, 16);
    Font FONT_BUTTON_SECONDARY = new Font("SansSerif", Font.BOLD, 15);

    Font FONT_LABEL_BOLD = new Font("SansSerif", Font.BOLD, 14);
    Font FONT_LABEL_PLAIN = new Font("SansSerif", Font.PLAIN, 14);

    Font FONT_TEXTFIELD = new Font("SansSerif", Font.PLAIN, 15);
    Font FONT_LINK = new Font("SansSerif", Font.BOLD, 14);

    
    int PADDING_SCREEN_DEFAULT = 40;
    int PADDING_COMPONENT_DEFAULT = 15;
    int PADDING_INTER_COMPONENT_VERTICAL = 20;
    int PADDING_INTER_COMPONENT_HORIZONTAL = 10;
    int PADDING_LARGE = 40;

    Border BORDER_TEXTFIELD_DEFAULT = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
    );
    Border BORDER_TEXTFIELD_FOCUSED = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARY_ACTION, 2),
            BorderFactory.createEmptyBorder(7, 11, 7, 11)
    );
    Border BORDER_BUTTON_PRIMARY = BorderFactory.createEmptyBorder(
            PADDING_COMPONENT_DEFAULT, PADDING_COMPONENT_DEFAULT * 2, PADDING_COMPONENT_DEFAULT, PADDING_COMPONENT_DEFAULT * 2
    );
}