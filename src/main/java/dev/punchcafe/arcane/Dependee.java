package dev.punchcafe.arcane;

@SpellBookPage
public class Dependee {

    private Dependant dependant;

    @Incantation
    public Dependee(Dependant dependant){
        this.dependant = dependant;
    }
}
