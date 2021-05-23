package util.dto;

public enum Label {
    /*--------- Errors ----------*/
    MAIN_PAGE_URL_ERROR(1),
    MAIN_LIST_ADDRESS_ERROR(2),
    CONTENT_LINK_ERROR(3),
    LOGO_LABEL_CONNECTION_ERROR(4),
    CURRENCY_CONNECTION_ERROR(5),
    HEADER_NOT_SELECTED_ERROR(6),
    FILE_NOT_FOUND(7),
    EMPTY_PREFERENCES_ERROR(8),
    SOURCE_NOT_SELECTED(9),
    SHARE_BUTTON_ERROR(10),
    /*--------- Messages ----------*/
    FIRST_TIME_MESSAGE(21),
    WELCOME_MESSAGE(22),
    SHARE_BUTTON_MESSAGE(23),
    COMBOBOX_TOOLTIP(24),
    CONNECTION_TOOLTIP(25),
    LOGO_LABEL_TOOLTIP(26),
    CONTENTS_TITLE(27),
    CONTENTS_DETAILS(28),
    LOG_SOURCE_ADDED(29),
    LOG_SOURCE_REMOVED(30),
    LANGUAGE_CHANGED(31),
    /*--------- Labels ----------*/
    MENU(41),
    SOURCE_MANAGEMENT(42),
    THEME(43),
    DARK(44),
    LIGHT(45),
    CURRENCY(46),
    USD_LABEL(47),
    EURO_LABEL(48),
    POUND_LABEL(49),
    GOLD_LABEL(50),
    INT_LABEL(51),
    HEADERS_LABEL(52),
    CONTENTS_LABEL(53),
    CHECKBOX_LABEL(54),
    LANGUAGE(55),
    /*--------- Buttons ----------*/
    CONNECTION_CONNECT_TEXT(71),
    CONNECTION_DISCONNECT_TEXT(72),
    EXAMINE_TEXT(73),
    READ_MORE_TEXT(74),
    SHARE_TEXT(75),
    EXIT(76),
    CONFIRM(77);

    private final int code;

    Label(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
