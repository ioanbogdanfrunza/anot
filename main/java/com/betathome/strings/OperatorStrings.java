package com.betathome.strings;

public class OperatorStrings {
    /** COM **/
    // App Error Alert Dialog
    private static final String comAppErrorTitle = "No app available!";
    private static final String comAppErrorMessage = "We couldn’t find a suitable installed app to handle this request.";
    private static final String comAppErrorBttn = "Ok";

    public static String getComAppErrorTitle() {
        return comAppErrorTitle;
    }
    public static String getComAppErrorMessage() {
        return comAppErrorMessage;
    }
    public static String getComAppErrorBttn() {
        return comAppErrorBttn;
    }

    // App Update Alert Dialog
    private static final String comAppUpdateTitle = "New version available";
    private static final String comAppUpdateMessage = "There is a newer version of this app available for installation. Do you wish to update?";
    private static final String comAppUpdateAcceptBttn = "Update";
    private static final String comAppUpdateCancelBttn = "Cancel";

    public static String getComAppUpdateTitle() {
        return comAppUpdateTitle;
    }

    public static String getComAppUpdateMessage() {
        return comAppUpdateMessage;
    }

    public static String getComAppUpdateAcceptBttn() {
        return comAppUpdateAcceptBttn;
    }

    public static String getComAppUpdateCancelBttn() {
        return comAppUpdateCancelBttn;
    }

    /** DE **/
    // App Error Alert Dialog
    private static final String deAppErrorTitle = "Keine App verfügbar!";
    private static final String deAppErrorMessage = "Wir konnten keine geeignete installierte App finden, um diese Anfrage zu bearbeiten.";
    private static final String deAppErrorBttn = "Ok";

    public static String getDeAppErrorTitle() {
        return deAppErrorTitle;
    }
    public static String getDeAppErrorMessage() {
        return deAppErrorMessage;
    }
    public static String getDeAppErrorBttn() {
        return deAppErrorBttn;
    }

    // App Update Alert Dialog
    private static final String deAppUpdateTitle = "Neue App-Version!";
    private static final String deAppUpdateMessage = "Es ist eine neuere Version dieser App zur Installation verfügbar. Möchten Sie es aktualisieren?";
    private static final String deAppUpdateAcceptBttn = "Aktualisieren";
    private static final String deAppUpdateCancelBttn = "Abbrechen";

    public static String getDeAppUpdateTitle() {
        return deAppUpdateTitle;
    }
    public static String getDeAppUpdateMessage() {
        return deAppUpdateMessage;
    }
    public static String getDeAppUpdateAcceptBttn() {
        return deAppUpdateAcceptBttn;
    }
    public static String getDeAppUpdateCancelBttn() {
        return deAppUpdateCancelBttn;
    }
}
