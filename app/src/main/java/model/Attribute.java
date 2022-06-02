package model;


public class Attribute {
    private long adc1 = 00;
    private long fuel = 00;
    private long io255 = 00;        //overspeeding
    private long io253 = 00;        //green driving type
    private long io254 = 00;        //green driving value
    private long io6 = 00;
    private long io9 = 00;
    private float power = 00;
    private long io251 = 00;

    public Attribute(float power, long adc1, long fuel, long io25, long io6, long io9, long io251, long io253, long io254, long io255) {
        this.adc1 = adc1;
        this.fuel = fuel;
        this.io251 = io25;
        this.io6 = io6;
        this.io9 = io9;
        this.io251 = io251;
        this.io253 = io253;
        this.io254 = io254;
        this.io255 = io255;
        this.power = power;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public long getIo255() {
        return io255;
    }

    public void setIo255(long io255) {
        this.io255 = io255;
    }

    public long getIo253() {
        return io253;
    }

    public void setIo253(long io253) {
        this.io253 = io253;
    }

    public long getIo254() {
        return io254;
    }

    public void setIo254(long io254) {
        this.io254 = io254;
    }

    public long getIo6() {
        return io6;
    }

    public void setIo6(long io6) {
        this.io6 = io6;
    }

    public long getIo9() {
        return io9;
    }

    public void setIo9(long io9) {
        this.io9 = io9;
    }

    public long getIo251() {
        return io251;
    }

    public void setIo251(long io251) {
        this.io251 = io251;
    }

    public long getAdc1() {
        return adc1;
    }

    public void setAdc1(long adc1) {
        this.adc1 = adc1;
    }

    public long getFuel() {
        return fuel;
    }

    public void setFuel(long fuel) {
        this.fuel = fuel;
    }

    @Override
    public String toString() {
        return getAdc1() + " " + getFuel() + " " + getIo255() + " " + getIo254() + " " + getIo251() + " " + getIo253() + " " + getIo6() + " " + getIo9();
    }
}
