package die.cafe;

@SpellBookPage
public class DescendantDependee {

    private Dependee dependant;

    public DescendantDependee(Dependee dependant){
        this.dependant = dependant;
    }
}
