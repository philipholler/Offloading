package p7gruppe.p7.offloading.api.dataclasses;

import p7gruppe.p7.offloading.data.enitity.AssignmentEntity;

import java.util.ArrayList;

public class ConfidenceResult{
    private double confidenceLevel;
    private String bestFilePath;
    private ArrayList<Long> correctAssignmentIds = new ArrayList<>();
    private ArrayList<Long> correctTestAssignmentIds = new ArrayList<>();

    public ConfidenceResult() {
    }

    public ConfidenceResult(double confidenceLevel, String bestFilePath, ArrayList<Long> correctAssignmentIds, ArrayList<Long> correctTestAssignmentIds) {
        this.confidenceLevel = confidenceLevel;
        this.bestFilePath = bestFilePath;
        this.correctAssignmentIds = correctAssignmentIds;
        this.correctTestAssignmentIds = correctTestAssignmentIds;
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

    public ArrayList<Long> getCorrectAssignmentIds() {
        return correctAssignmentIds;
    }

    public ArrayList<Long> getCorrectTestAssignmentIds() {
        return correctTestAssignmentIds;
    }

    @Override
    public String toString() {
        return "ConfidenceResult{" +
                "confidenceLevel=" + confidenceLevel +
                ", bestFilePath='" + bestFilePath + '\'' +
                ", correctDevices=" + correctAssignmentIds +
                '}';
    }

    public boolean hasCorrectAnswerFromAssignment(AssignmentEntity assignment){
        return this.correctAssignmentIds.contains(assignment.getAssignmentId())
                || correctTestAssignmentIds.contains(assignment.getAssignmentId());
    }
}
