package com.erg.memorized.model;

public class Score {

    private int hitsCont;
    private int almostHitsCont;
    private int missCont;
    private int ops; //options

    public Score(int hitsCont, int almostHitsCont,
                 int missCont, int ops) {
        this.hitsCont = hitsCont;
        this.almostHitsCont = almostHitsCont;
        this.missCont = missCont;
        this.ops = ops;
    }

    public int getHitsCont() {
        return hitsCont;
    }

    public void setHitsCont(int hitsCont) {
        this.hitsCont = hitsCont;
    }


    public int getAlmostHitsCont() {
        return almostHitsCont;
    }

    public void setAlmostHitsCont(int almostHitsCont) {
        this.almostHitsCont = almostHitsCont;
    }

    public int getMissCont() {
        return missCont;
    }

    public void setMissCont(int missCont) {
        this.missCont = missCont;
    }

    public int getOps() {
        return ops;
    }

    public void setOps(int ops) {
        this.ops = ops;
    }

    @Override
    public String toString() {
        return "Score{" +
                "hitsCont=" + hitsCont +
                ", almostHitsCont=" + almostHitsCont +
                ", missCont=" + missCont +
                ", totalCont=" + ops +
                '}';
    }
}
