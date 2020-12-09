package p7gruppe.p7.offloading.api.dataclasses;

import java.util.ArrayList;

public class ConfidenceResult{
    private double confidenceLevel;
    private String bestFilePath;
    private ArrayList<Integer> correctDevices = new ArrayList<>();

    public ConfidenceResult() {
    }

    public ConfidenceResult(double confidenceLevel, String bestFilePath, ArrayList<Integer> correctDevices) {
        this.confidenceLevel = confidenceLevel;
        this.bestFilePath = bestFilePath;
        this.correctDevices = correctDevices;
    }

    public double getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(double confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public String getBestFilePath() {
        return bestFilePath;
    }

    public void setBestFilePath(String bestFilePath) {
        this.bestFilePath = bestFilePath;
    }

    public ArrayList<Integer> getCorrectDevices() {
        return correctDevices;
    }

    public void setCorrectDevices(ArrayList<Integer> correctDevices) {
        this.correctDevices = correctDevices;
    }

    @Override
    public String toString() {
        return "ConfidenceResult{" +
                "confidenceLevel=" + confidenceLevel +
                ", bestFilePath='" + bestFilePath + '\'' +
                ", correctDevices=" + correctDevices +
                '}';
    }
}
