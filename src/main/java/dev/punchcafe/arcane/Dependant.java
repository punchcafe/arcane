package dev.punchcafe.arcane;

@SpellBookPage
public class Dependant {

    @Incantation
    public Dependant(){};

    @SpellName(name = "append")
    public static Dependant someFactory(){
        return new Dependant();
    }

}
