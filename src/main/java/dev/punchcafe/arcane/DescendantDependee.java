package dev.punchcafe.arcane;

@SpellBookPage
public class DescendantDependee {

    private Dependee dependant;

    @Incantation
    public DescendantDependee(Dependee dependant){
        this.dependant = dependant;
    }
}
