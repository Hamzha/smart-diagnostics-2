package model;

public class DialogBox {

    private Position position;
    private String iconName;

    public DialogBox(Position position, String iconName) {
        this.position = position;
        this.iconName = iconName;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconname) {
        this.iconName = iconname;
    }
}
