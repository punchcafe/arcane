package dev.punchcafe.arcane;

@SpellBookPage
public class SpecificDependant {

    private Dependant dependant;

    @Incantation
    public SpecificDependant(@SpellName(name = "append")Dependant dependant){
        this.dependant = dependant;
    }
}
