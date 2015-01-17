package noah.poolgamescorer.main;

public class Contact {

    private boolean valid;
    private String name;
    private String number;

    public Contact() {
        valid = false;
        name = "";
        number = "";
    }

    public boolean getValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
