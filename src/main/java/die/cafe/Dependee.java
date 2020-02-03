package die.cafe;

@SpellBookPage
public class Dependee {

    private Dependant dependant;

    public Dependee(Dependant dependant){
        this.dependant = dependant;
    }
}
