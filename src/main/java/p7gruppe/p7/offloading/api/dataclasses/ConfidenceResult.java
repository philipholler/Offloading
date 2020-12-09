package p7gruppe.p7.offloading.api.dataclasses;

import java.util.ArrayList;

public class ConfidenceResult{
    private double confidenceLevel;
    private String bestFilePath;
    private ArrayList<Integer> correctWorkers = new ArrayList<>();
    private ArrayList<Integer> correctTestWorkers = new ArrayList<>();

    public ConfidenceResult() {
    }

    public ConfidenceResult(double confidenceLevel, String bestFilePath, ArrayList<Integer> correctWorkers, ArrayList<Integer> correctTestWorkers) {
        this.confidenceLevel = confidenceLevel;
        this.bestFilePath = bestFilePath;
        this.correctWorkers = correctWorkers;
        this.correctTestWorkers = correctTestWorkers;
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

    public ArrayList<Integer> getCorrectWorkers() {
        return correctWorkers;
    }

    public void setCorrectWorkers(ArrayList<Integer> correctWorkers) {
        this.correctWorkers = correctWorkers;
    }

    public ArrayList<Integer> getCorrectTestWorkers() {
        return correctTestWorkers;
    }

    @Override
    public String toString() {
        return "ConfidenceResult{" +
                "confidenceLevel=" + confidenceLevel +
                ", bestFilePath='" + bestFilePath + '\'' +
                ", correctDevices=" + correctWorkers +
                '}';
    }
}
