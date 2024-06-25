package com.br.luminous.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int estimatedTime;
    private int power;
    private String deviceName;
    private Double kWhMonthly;
    private Double kwhDaily;
    private boolean isPriority;
    private boolean isCompleted = false;

    public Challenge(int estimatedTime, int power, String deviceName) {
        this.estimatedTime = estimatedTime;
        this.power = power;
        this.deviceName = deviceName;
        this.kWhMonthly = Challenge.calculateKwhMonthly(estimatedTime, power);
        this.kwhDaily = Challenge.calculateKwhDaily(estimatedTime, power);
        this.isPriority = this.kwhDaily > 5.0;
    }

    static public double calculateKwhMonthly(int estimatedTime, int power) {
        return (estimatedTime * power / 1000.0) * 30;
    }

    static public double calculateKwhDaily(int estimatedTime, int power) {
        return (estimatedTime * power / 1000.0);
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Double getKWHMonthly() {
        return this.kWhMonthly;
    }

    public boolean isPriority() {
        return isPriority;
    }

    public void makeChallengePriority() {
        this.isPriority = true;
    }

    public void completeChallenge() {
        this.isCompleted = true;
        this.isPriority = false;
    }

    public boolean isCompleted() {
        return isCompleted;
    }
}
